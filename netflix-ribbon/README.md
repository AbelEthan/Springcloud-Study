# Spring Cloud Netflix Ribbon 源码分析

## 利用 RibbonLoadBalancerClient

### 新建一个工程

三个模块：

* user-api：公用 API
* user-ribbon-client：客户端应用
* user-service-provider：服务端应用

#### 实现 user-ribbon-client

##### 配置信息

`application.yml`:

```yaml
# 用户 Ribbon 客户端应用
spring:
  application:
    name: user-ribbon-client

# 服务端口
server:
  port: 8080

# 提供方服务名称
# 提供方服务主机
# 提供方服务端口
provider:
  service:
    name: user-service-provider
    host: localhost
    port: 9090

# 关闭 Eureka Client，显示地通过配置方式注册 Ribbon 服务地址
eureka:
  client:
    enabled: false

# 定义 user-service-provider Ribbon 的服务器地址
# 为 RibbonLoadBalancerClient 提供服务列表
user-service-provider:
  ribbon:
    listOfServers: http://${provider.service.host}:${provider.service.port}
```

##### 编写客户端调用

```java
package com.springcloud.ribbon.service.web.controller;

import com.springcloud.ribbon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

/**
 * Title: 用户 Ribbon Controller
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/22 18:50
 */
@RestController
public class UserRibbonController {

//    @Autowired
//    private RestTemplate restTemplate;

    /**
     *  负载均衡器客户端
     */
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Value("${provider.service.name}")
    private String providerServiceName;

    @GetMapping("")
    public String index() throws IOException {
        User user = new User();
        user.setId(1003L);
        user.setName("abel");
        ServiceInstance serviceInstance = loadBalancerClient.choose(providerServiceName);
        return loadBalancerClient.execute(providerServiceName, serviceInstance, instance ->{
            String host = instance.getHost();
            int port = instance.getPort();
            String url = "http://" + host + ":" + port + "/user/save";
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.postForObject(url, user, String.class);
        });
    }
}
```

##### 编写引导类

```java
package com.springcloud.ribbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.ribbon.RibbonClient;

/**
 * Title:
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/22 21:43
 */
@SpringBootApplication
@RibbonClient("user-service-provider")
public class UserRibbonClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserRibbonClientApplication.class, args);
    }
}
```

#### 实现 user-service-provider

##### 配置信息

`application.yml`:

```yaml
# 用户服务提供方应用信息
spring:
  application:
    name: user-service-provider

# 服务端口
server:
  port: 9090

# 关闭 Eureka Client，显示地通过配置方式注册 Ribbon 服务地址
eureka:
  client:
    enabled: false
```

##### 实现 `UserService`

```java
package com.springcloud.ribbon.service;

import com.springcloud.ribbon.api.UserService;
import com.springcloud.ribbon.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: 内存实现 {@link UserService}
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/22 18:36
 */
@Service
public class InMemoryUserService implements UserService {

    private Map<Long, User> repository = new ConcurrentHashMap<>();
    @Override
    public boolean savaUser(User user) {
        return repository.put(user.getId(), user) == null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(repository.values());
    }
}
```

##### 实现 Web 服务

```java
package com.springcloud.ribbon.service.web.controller;

import com.springcloud.ribbon.api.UserService;
import com.springcloud.ribbon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Title:
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/23 9:32
 */
@RestController
public class UserServiceProviderController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/save")
    public boolean user(@RequestBody User user){
        return userService.saveUser(user);
    }

}
```

##### 编写引导类

```java
package com.springcloud.ribbon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Title:
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/23 9:35
 */
@SpringBootApplication
public class UserServiceProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceProviderApplication.class, args);
    }
}
```

#### 分析调用链路

##### 选择服务器逻辑

LoadBalancerClient（LoadBalancerClient） -> ILoadBalancer（ZoneAwareLoadBalancer） ->  IRule (ZoneAvoidanceRule)

#### 自定义实现 `IRule`

##### 扩展 `AbstractLoadBalancerRule` ： `MyRule`

```java
package com.segumentfault.spring.cloud.lesson7.user.ribbon.client.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * 自定义{@link IRule} 实现，永远选择最后一台可达服务器
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
public class MyRule extends AbstractLoadBalancerRule {

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {

    }

    @Override
    public Server choose(Object key) {

        ILoadBalancer loadBalancer = getLoadBalancer();

        //获取所有可达服务器列表
        List<Server> servers = loadBalancer.getReachableServers();
        if (servers.isEmpty()) {
            return null;
        }

        // 永远选择最后一台可达服务器
        Server targetServer = servers.get(servers.size() - 1);
        return targetServer;
    }

}
```

##### 将 `MyRule` 暴露成 `Bean`

> 通过`RibbonClientConfiguration`学习源码：
>
> ```java
> @Bean
> @ConditionalOnMissingBean
> public IRule ribbonRule(IClientConfig config) {
>    if (this.propertiesFactory.isSet(IRule.class, name)) {
>       return this.propertiesFactory.get(IRule.class, config, name);
>    }
>    ZoneAvoidanceRule rule = new ZoneAvoidanceRule();
>    rule.initWithNiwsConfig(config);
>    return rule;
> }
> ```

```java
/**
 * 将 {@link MyRule} 暴露成 {@link Bean}
 *
 * @return {@link MyRule}
 */
@Bean
public IRule myRule() {
    return new MyRule();
}
```

#### 配置化实现组件

> 通过学习`PropertiesFactory` 源码：
>
> ```java
> 	public PropertiesFactory() {
> 		classToProperty.put(ILoadBalancer.class, "NFLoadBalancerClassName");
> 		classToProperty.put(IPing.class, "NFLoadBalancerPingClassName");
> 		classToProperty.put(IRule.class, "NFLoadBalancerRuleClassName");
> 		classToProperty.put(ServerList.class, "NIWSServerListClassName");
> 		classToProperty.put(ServerListFilter.class, "NIWSServerListFilterClassName");
> 	}
> ```
>
> 可知`NFLoadBalancerClassName` 等是可以配置的

#####  实现 `IPing` : `MyPing`

```java
package com.springcloud.ribbon.client.ping;

import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.Server;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Title: 实现 {@link IPing} 接口
 * Description:  检查对象 /health
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/25 10:01
 */
public class ClientPing implements IPing {
    @Override
    public boolean isAlive(Server server) {
        String host = server.getHost();
        int port = server.getPort();
        // /health endpoint
        //通过 Spring 组建来实现URL 拼装
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        builder.scheme("http");
        builder.host(host);
        builder.port(port);
        builder.path("/health");

        URI uri = builder.build().toUri();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity responseEntity = restTemplate.getForEntity(uri, String.class);
        //当响应状态等于 200 时， 返回 true ， 否则 false
        return HttpStatus.OK.equals(responseEntity.getStatusCode());
    }
}

```

##### 增加配置

`application.properties`:

```properties
## 扩展 IPing 实现
user-service-provider.ribbon.NFLoadBalancerPingClassName = \
  com.segumentfault.spring.cloud.lesson7.user.ribbon.client.ping.MyPing
```





