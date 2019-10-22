# [Spring Cloud 服务调用](https://github.com/AbelEthan/spring-cloud/tree/master/service-invocation)

## 核心概念

## Spring Cloud Feign
### 增加 spring-cloud-starter-feign 依赖

```xml
        <!-- 添加 Spring Cloud Feign 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-feign</artifactId>
        </dependency>
```

### 申明 Feign 客户端

```java
package com.springcloud.service.api;

import com.springcloud.service.entity.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Title: 用戶服务
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/22 18:30
 */
// 利用占位符避免未来整合硬编码
@FeignClient(name = "${user.service.name}")
public interface UserService {

    /**
     * 保存用户
     * @param user
     * @return
     */
    @PostMapping("/user/save")
    boolean saveUser(User user);

    /**
     * 查询所有的用户列表
     *
     * @return non-null
     */
    @GetMapping("/user/find/all")
    List<User> findAll();
}
```

> 注意，在使用`@FeignClient`  `name` 属性尽量使用占位符，避免硬编码。否则，未来升级时，不得不升级客户端版本。

### 激活 FeignClient

`@EnableFeigntClients`

```java
package com.springcloud.service;

import com.netflix.loadbalancer.IRule;
import com.springcloud.service.api.UserService;
import com.springcloud.service.client.rule.ClientRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Title:
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/22 21:43
 */
@SpringBootApplication
// 指定目标应用名称
@RibbonClient("user-service-provider")
//使用服务短路
@EnableCircuitBreaker
// 申明 UserService 接口作为 Feign Client 调用
@EnableFeignClients(clients = UserService.class)
public class UserServiceClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceClientApplication.class, args);
    }

    /**
     * 将 {@link ClientRule} 暴露成 {@link Bean}
     *
     * @return {@link ClientRule}
     */
    @Bean
    public IRule myRule(){
        return new ClientRule();
    }

    /**
     * 申明 具有负载均衡能力 {@link RestTemplate}
     *
     * @return
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}
```

## Spring Cloud 整合

### 整合负载均衡：Netflix Ribbon

### 客户端：激活`@FeignClient` `UserService`

```java
package com.segumentfault.spring.cloud.lesson10.user.service.client;

import com.netflix.loadbalancer.IRule;
import com.segumentfault.spring.cloud.lesson10.api.UserService;
import com.segumentfault.spring.cloud.lesson10.user.service.client.rule.MyRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * 引导类
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 0.0.1
 */
@SpringBootApplication
@RibbonClient("user-service-provider") // 指定目标应用名称
@EnableCircuitBreaker // 使用服务短路
@EnableFeignClients(clients = UserService.class) // 申明 UserService 接口作为 Feign Client 调用
public class UserServiceClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceClientApplication.class, args);
    }

    /**
     * 将 {@link MyRule} 暴露成 {@link Bean}
     *
     * @return {@link MyRule}
     */
    @Bean
    public IRule myRule() {
        return new MyRule();
    }

    /**
     * 申明 具有负载均衡能力 {@link RestTemplate}
     *
     * @return
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
```

#### 客户端：配置@FeignClient(name = "${user.service.name}") 中的占位符

调整 `application.yml`

```yaml
# 用户 Ribbon 客户端应用
spring:
  application:
    name: user-service-client

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
# 扩展IPing 实现
user-service-provider:
  ribbon:
    listOfServers: http://${provider.service.host}:${provider.service.port}
    NFLoadBalancerPingClassName: com.springcloud.service.client.ping.ClientPing

# 配置 @FeignClient(name = "${user.service.name}") 中的占位符
# user.service.name 实际需要制定 UserService 接口的提供方
# 也就是 user-service-provider，可以使用 ${provider.service.name} 替代
user:
  service:
    name: ${provider.service.name}
```



### 服务端：实现`UserService` ，即暴露 HTTP REST 服务

调整应用：`user-service-provider`

#### 增加 `InMemoryUserService` 的Bean 名称

```java
package com.springcloud.service.provider.impl;

import com.springcloud.service.api.UserService;
import com.springcloud.service.entity.User;
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
@Service("inMemoryUserService")
public class InMemoryUserService implements UserService {

    private Map<Long, User> repository = new ConcurrentHashMap<>();

    @Override
    public boolean saveUser(User user) {
        System.out.println(repository);
        return repository.put(user.getId(), user) == null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(repository.values());
    }
}
```

#### `UserServiceProviderController` 实现 Feign 客户端接口`UserService`

`UserServiceProviderController.java `

```java
package com.springcloud.service.provider.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springcloud.service.api.UserService;
import com.springcloud.service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Title: 用户服务提供方 Controller
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/23 9:32
 */
@RestController
public class UserServiceProviderController implements UserService{

    @Autowired
    @Qualifier("inMemoryUserService")
    private UserService userService;

    private final static Random random = new Random();

    @Override
    public boolean saveUser(@RequestBody User user){
        return userService.saveUser(user);
    }

    @Override
    public List<User> findAll() {
        return userService.findAll();
    }

    /**
     * 获取所有用户列表
     *
     * @return
     */
    @HystrixCommand(
            // Command 配置
            commandProperties = {
                    // 设置操作时间为 100 毫秒
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100")
            },
            fallbackMethod = "fallbackForGetUsers"
    )
    @GetMapping("/list")
    public List<User> getUsers() throws InterruptedException {
        int executeTime = random.nextInt(200);
        //通过休眠来模拟执行时间
        System.out.println("Execute Time : " + executeTime + " ms");
        Thread.sleep(executeTime);
        return userService.findAll();
    }

    /**
     * {@link #getUsers()} 的 fallback 方法
     *
     * @return 空集合
     */
    public List<User> fallbackForGetUsers(){
        return Collections.emptyList();
    }

}
```

### 客户端：使用 `UserService` 直接调用远程 HTTP REST 服务

```java
package com.springcloud.service.client.web;

import com.springcloud.service.api.UserService;
import com.springcloud.service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Title: {@link UserServiceClientController} 客户端 {@link RestController}
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/11/2 11:17
 */
@RestController
public class UserServiceClientController implements UserService {

    @Autowired
    private UserService userService;

    // 通过方法继承，URL 映射 ："/user/save"
    @Override
    public boolean saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // 通过方法继承，URL 映射 ："/user/find/all"
    @Override
    public List<User> findAll() {
        return userService.findAll();
    }
}
```

### 整合服务短路：Netflix Hystrix

#### API：调整`UserService` 并且实现 Fallback

`UserService` Fallback 实现

```java
package com.springcloud.service.fallback;

import com.springcloud.service.api.UserService;
import com.springcloud.service.entity.User;

import java.util.Collections;
import java.util.List;

/**
 * Title: {@link UserServiceFallback} Fallback 实现
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/11/2 11:29
 */
public class UserServiceFallback implements UserService {
    @Override
    public boolean saveUser(User user) {
        return false;
    }

    @Override
    public List<User> findAll() {
        return Collections.emptyList();
    }
}
```

调整 `UserService` `@FeignClient` fallback 属性：

```java
package com.springcloud.service.api;

import com.springcloud.service.entity.User;
import com.springcloud.service.fallback.UserServiceFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Title: 用戶服务
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/22 18:30
 */
// 利用占位符避免未来整合硬编码
@FeignClient(name = "${user.service.name}", fallback = UserServiceFallback.class)
public interface UserService {

    /**
     * 保存用户
     * @param user
     * @return
     */
    @PostMapping("/user/save")
    boolean saveUser(User user);

    /**
     * 查询所有的用户列表
     *
     * @return non-null
     */
    @GetMapping("/user/find/all")
    List<User> findAll();
}
```

#### 服务端： `UserServiceProviderController#findAll()` 方法整合 `@HystrixCommand`

```java
package com.springcloud.service.provider.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springcloud.service.api.UserService;
import com.springcloud.service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Title: 用户服务提供方 Controller
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/23 9:32
 */
@RestController
public class UserServiceProviderController implements UserService{

    @Autowired
    @Qualifier("inMemoryUserService")
    private UserService userService;

    private final static Random random = new Random();

    @Override
    public boolean saveUser(@RequestBody User user){
        return userService.saveUser(user);
    }

    @HystrixCommand(
            // Command 配置
            commandProperties = {
                    // 设置操作时间为 100 毫秒
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100")
            },
            fallbackMethod = "fallbackForGetUsers"
    )
    @Override
    public List<User> findAll() {
        return userService.findAll();
    }

    /**
     * 获取所有用户列表
     *
     * @return
     */
    @HystrixCommand(
            // Command 配置
            commandProperties = {
                    // 设置操作时间为 100 毫秒
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100")
            },
            fallbackMethod = "fallbackForGetUsers"
    )
    @GetMapping("/list")
    public List<User> getUsers() throws InterruptedException {
        int executeTime = random.nextInt(200);
        //通过休眠来模拟执行时间
        System.out.println("Execute Time : " + executeTime + " ms");
        Thread.sleep(executeTime);
        return userService.findAll();
    }

    /**
     * {@link #getUsers()} 的 fallback 方法
     *
     * @return 空集合
     */
    public List<User> fallbackForGetUsers(){
        return Collections.emptyList();
    }

}
```

### 整合服务发现：Netflix Eureka

#### 创建 Eureka Server

##### `pom.xml` 增加 Eureka Server 依赖

```xml
    <dependencies>

        <!-- Eureka Server 依赖 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka-server</artifactId>
        </dependency>

    </dependencies>
```

##### 创建引导类：`EurekaServerApplication`

```java
package com.springcloud.eureka.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Title: EurekaServer引导类
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/11/2 16:33
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

##### 配置 Eureka Server

`application.yml`

```yaml
# Spring Cloud Eureka 服务器应用名称
spring:
  application:
    name: spring-cloud-eureka-server

# Spring Cloud Eureka 服务器服务端口
server:
  port: 8761

# 管理端口安全失效
management:
  security:
    enabled: false

# Spring Cloud Eureka 服务器作为注册中心
# 通常情况下，不需要再注册到其他注册中心去
# 同时，它也不需要获取客户端信息
# 取消向注册中心注册
eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    # 取消向注册中心获取注册信息（服务、实例信息）
    fetch-registry: false
    # 解决 Peer / 集群 连接问题
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka
```

> 端口信息
>
> ​	user-service-client : 8080
>
> ​	user-service-provider: 9090
>
> ​  eureka-server : 8761



#### 客户端：配置服务发现客户端

配置应用：`user-service-client`

##### `pom.xml` 增加 eureka-client 依赖

```xml
<!-- 依赖 Spring Cloud Netflix Eureka -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

##### 激活服务发现客户端

`UserServiceClientApplication.java`

```java
package com.springcloud.service;

import com.netflix.loadbalancer.IRule;
import com.springcloud.service.api.UserService;
import com.springcloud.service.client.rule.ClientRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Title:
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/22 21:43
 */
@SpringBootApplication
// 指定目标应用名称
@RibbonClient("user-service-provider")
//使用服务短路
@EnableCircuitBreaker
// 申明 UserService 接口作为 Feign Client 调用
@EnableFeignClients(clients = UserService.class)
// 激活服务发现客户端
@EnableDiscoveryClient
public class UserServiceClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceClientApplication.class, args);
    }

    /**
     * 将 {@link ClientRule} 暴露成 {@link Bean}
     *
     * @return {@link ClientRule}
     */
    @Bean
    public IRule myRule(){
        return new ClientRule();
    }

    /**
     * 申明 具有负载均衡能力 {@link RestTemplate}
     *
     * @return
     */
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate(){
        return  new RestTemplate();
    }
}
```

##### 配置 Eureka 注册中心

`application.yml`

```yaml
# 用户 Ribbon 客户端应用
spring:
  application:
    name: user-service-client

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

# 激活 Eureka Client，显示地通过配置方式注册 Ribbon 服务地址
eureka:
  client:
    enabled: true
    serviceUrl:
      defaultZone: http://localhost:8761/eureka

# 定义 user-service-provider Ribbon 的服务器地址
# 为 RibbonLoadBalancerClient 提供服务列表
# 扩展IPing 实现
user-service-provider:
  ribbon:
    listOfServers: http://${provider.service.host}:${provider.service.port}
    NFLoadBalancerPingClassName: com.springcloud.service.client.ping.ClientPing

# 配置 @FeignClient(name = "${user.service.name}") 中的占位符
# user.service.name 实际需要制定 UserService 接口的提供方
# 也就是 user-service-provider，可以使用 ${provider.service.name} 替代
user:
  service:
    name: ${provider.service.name}

# 服务管理员信息
info:
  作者: AbelEthan
  邮件: abelethan@126.com
  github: github.com/abelethan
  马云: gitee.com/abelethan
```

#### 服务端：配置服务发现客户端

配置应用：`user-service-provider`

##### `pom.xml` 增加 eureka-client 依赖

```xml
        <!-- 依赖 Spring Cloud Netflix Eureka -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
```

##### 激活服务发现客户端

`UserServiceProviderApplication.java`

```java
package com.springcloud.service.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * Title: 引导类
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/23 9:35
 */
@SpringBootApplication
@EnableHystrix
// 激活服务发现客户端
@EnableDiscoveryClient
public class UserServiceProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceProviderApplication.class, args);
    }
}
```

##### 配置 Eureka 注册中心

`application.yml`

```yaml
# 用户服务提供方应用信息
spring:
  application:
    name: user-service-provider

# 服务端口
server:
  port: 9090

# 激活 Eureka Client，显示地通过配置方式注册 Ribbon 服务地址
eureka:
  client:
    enabled: true
    serviceUrl:
      defaultZone: http://localhost:8761/eureka

# 服务管理员信息
info:
  作者: AbelEthan
  邮件: abelethan@126.com
  github: github.com/abelethan
  马云: gitee.com/abelethan
```

### 整合配置服务器：Config Server

#### 创建 Config Server

#### `pom.xml` 增加 Config Server 依赖

```xml
    <dependencies>

        <!-- 依赖 Spring Cloud Config Server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>

    </dependencies>
```

#### 基于文件系统（File System）配置

> 注意：user-service-client  application.properties 中以下内容将会被配置服务器中的 user-service.properties 替代.

创建 `user-service.yml`

```yaml
## 提供方服务名称
# 提供方服务主机
# 提供方服务端口
provider:
  service:
    name: user-service-provider
    host: localhost
    port: 9090

# 配置 @FeignClient(name = "${user.service.name}") 中的占位符
# user.service.name 实际需要制定 UserService 接口的提供方
# 也就是 user-service-provider，可以使用 ${provider.service.name} 替代
user:
  service:
    name: ${provider.service.name}
```

##### 初始化配置文件根路径

目前${user.dir} 指向: `C:/Users/AbelEthan/Desktop/spring-cloud/service-invocation/`

`user-service.yml` 相对于 `/config-server/src/main/resources/conf-repo`

控制台执行 git 命令：

```
$ git init
Initialized empty Git repository in C:/Users/AbelEthan/Desktop/spring-cloud/service-invocation/config-server/src/main /resources/conf-repo/.git/
$ git add user-service.yml
$ git commit -m "add user-service.yml"
[master (root-commit) 61e2fa2] add user-service.yml
 1 file changed, 15 insertions(+)
 create mode 100644 user-service.yml
```

##### 设置配置文件根路径

`application.yml`

```yaml
## Spring Cloud Config Server 应用名称
spring.application.name = config-server

## 服务器服务端口
# Spring Cloud Config Server 应用名称
spring:
  application:
    name: spring-cloud-config-server

  ## 配置服务器文件系统git 仓库
  ## ${user.dir} 减少平台文件系统的不一致
  ## 目前 ${user.dir}/config-server/src/main/resources/conf-repo
  cloud:
    config:
      server:
        git:
          uri: ${user.dir}/config-server/src/main/resources/conf-repo

# 服务器服务端口
server:
  port: 7070

# 管理端口安全失效
management:
  security:
    enabled: false

## Spring Cloud Eureka 客户端 注册到 Eureka 服务器
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka
 ```

##### 激活服务发现客户端

`pom.xml`

```xml
<!-- 依赖 Spring Cloud Netflix Eureka -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

`application.yml`

```yaml
## Spring Cloud Eureka 客户端 注册到 Eureka 服务器
eureka:
  client:
    serviceUrl:
      defaultZone:  http://localhost:8761/eureka
```

激活服务发现

```java
package com.springcloud.config.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Title: 配置服务器应用
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/11/2 17:11
 */
@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

### 整合配置客户端：Config Client

调整应用 `user-service-client` ，作为 config-client 应用

#### pom.xml 增加 config client 依赖

```xml
<!-- 依赖 Spring Cloud Config Client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

#### 创建并且配置 `bootstrap.yml` 文件

`bootstrap.yml`

```yaml
# 用户 Ribbon 客户端应用
spring:
  application:
    name: user-service-client
  cloud:
    # 配置客户端应用关联的应用
    # spring.cloud.config.name 是可选的
    # 如果没有配置，采用 ${spring.application.name}
    # 关联 profile
    # 关联 label
    # 激活 Config Server 服务发现
    # Config Server 服务器应用名称
    config:
      name: user-service
      profile: default
      label: master
      discovery:
        enabled: true
        serviceId: spring-cloud-config-server

# Spring Cloud Eureka 客户端 注册到 Eureka 服务器
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka
```

`application.yml 也有调整`

```yaml
## 服务端口
# 服务端口
server:
  port: 8080

# 定义 user-service-provider Ribbon 的服务器地址
# 为 RibbonLoadBalancerClient 提供服务列表
# 扩展IPing 实现
user-service-provider:
  ribbon:
    NFLoadBalancerPingClassName: com.springcloud.service.client.ping.ClientPing

## 提供方服务名称
## 提供方服务主机
## 提供方服务端口
#provider:
#  service:
#    name: user-service-provider
#    host: localhost
#    port: 9090
## 配置 @FeignClient(name = "${user.service.name}") 中的占位符
## user.service.name 实际需要制定 UserService 接口的提供方
## 也就是 user-service-provider，可以使用 ${provider.service.name} 替代
#user:
#  service:
#    name: ${provider.service.name}

# 服务管理员信息
info:
  作者: AbelEthan
  邮件: abelethan@126.com
  github: github.com/abelethan
  马云: gitee.com/abelethan
```