# Spring Cloud 服务发现/注册

## Spring Cloud  Netflix Eureka

### Eureka 服务器

#### 引入 Maven 依赖

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-eureka-server</artifactId>
</dependency>
```

#### 激活 Eureka 服务器

```java
package com.springcloud.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

#### 调整 Eureka 服务器配置

`application.yml`

```yaml
# Spring Cloud Eureka 服务器应用名称
spring:
  application:
    name: spring-cloud-eureka-server

# Spring Cloud Eureka 服务器服务端口
server:
  port: 9090

# 管理端口安全实效
management:
  security:
    enabled: false
```

#### 检验 Eureka Server

异常堆栈信息：

```
[nfoReplicator-0] c.n.discovery.InstanceInfoReplicator     : There was a problem with the instance info replicator

com.netflix.discovery.shared.transport.TransportException: Cannot execute request on any known server
```

http://localhost:9090/

运行效果：

># Instances currently registered with Eureka

问题原因：Eureka Server 既是注册服务器，也是客户端，默认情况，也需要配置注册中心地址。

> "description": "Spring Cloud Eureka Discovery Client"

#### 解决问题的方法

```yaml
# Spring Cloud Eureka 服务器作为注册中心
# 通常情况下，不需要再注册到其他注册中心去
# 同时，它也不需要获取客户端信息
# 取消向注册中心注册
eureka:
  instance:
    hostname: localhost     # 解决 Peer / 集群 连接问题
  client:
    register-with-eureka: false
    fetch-registry: false       # 取消向注册中心获取注册信息（服务、实例信息）
    serviceUrl:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka
```

### Eureka 客户端

#### 引入 Maven 依赖

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

#### 激活 Eureka 客户端

```java
package com.springcloud.eurekaclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class EurekaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }
}
```

#### 配置 Eureka 客户端

```yaml
# Spring Cloud Eureka 客户端应用名称
spring:
  application:
    name: spring-cloud-eureka-client

# Spring Cloud Eureka 客户端端口
server:
  port: 8080

# 管理端口安全实效
management:
  security:
    enabled: false
```

#### 检验 Eureka 客户端

发现与 Eureka 服务器控制台出现相同异常：

```
[nfoReplicator-0] c.n.discovery.InstanceInfoReplicator     : There was a problem with the instance info replicator

com.netflix.discovery.shared.transport.TransportException: Cannot execute request on any known server
```

#### 需要再次调整 Eureka 客户端配置

```yaml
# Spring Cloud Eureka 客户端应用名称
spring:
  application:
    name: spring-cloud-eureka-client

# Spring Cloud Eureka 客户端端口
server:
  port: 8080

# 管理端口安全实效
management:
  security:
    enabled: false

## Spring Cloud Eureka 客户端 注册到 Eureka 服务器
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:9090/eureka/
```

### Spring Cloud Config 与 Eureka 整合

#### 调整 `spring-cloud-config-server` 作为 Eureka 客户端

#### 引入 Maven 依赖

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-eureka</artifactId>
</dependency>
```

#### 激活 Eureka 客户端

```java
package com.springcloud.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
@EnableDiscoveryClient
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

#### 调整 `spring-cloud-config-server` 配置

```yaml
# 配置服务器应用名称
spring:
  application:
    name: spring-cloud-config-server
  # 配置服务器文件系统git 仓库
  # ${user.dir} 减少平台文件系统的不一致
  cloud:
    config:
      server:
        git:
          force-pull: true      # 强制拉去 Git 内容
          uri: https://github.com/AbelEthan/spring-cloud-config-repo/       # 配置服务器远程 Git 仓库（GitHub）
          # uri: ${user.dir}/src/main/resources/configs
# 配置服务器端口
server:
  port: 7070

# 关闭管理端actuator 的安全
# /env /health 端口完全开放
management:
  security:
    enabled: false

# spring-cloud-config-server 注册到 Eureka 服务器
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:9090/eureka/
```

#### 调整 `spring-cloud-eureka-client` 称为 Config 客户端

##### 引入 `spring-cloud-starter-config` Maven 依赖

```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

##### 创建 `bootstrap.yml`

##### 配置 Config 客户端配置

`bootstrap.yml`：

```yaml
## 配置客户端应用关联的应用
## spring.cloud.config.name 是可选的
## 如果没有配置，采用 ${spring.application.name}
spring:
  cloud:
    config:
      name: neo-config
      profile: pro      # 关联 profile
      label: master     # 关联 label
      discovery:
        enabled: true     # 激活 Config 服务器发现
        serviceId: spring-cloud-config-server    # 配置 Config 服务器的应用名称（Service ID）
```

##### 检验效果

启动发现，spring-cloud-config-server 服务无法找到，原因如下：

```
注意：当前应用需要提前获取应用信息，那么将 Eureka 的配置信息提前至 bootstrap.yml 文件
原因：bootstrap 上下文是 Spring Boot 上下文的 父 上下文，那么它最先加载，因此需要最优先加载 Eureka 注册信息
```

##### 调整后配置

`bootstrap.yml`：

```yaml
# 注意：当前应用需要提前获取应用信息，那么将 Eureka 的配置信息提前至 bootstrap.yml 文件
# 原因：bootstrap 上下文是 Spring Boot 上下文的 父 上下文，那么它最先加载，因此需要最优先加载 Eureka 注册信息
# Spring Cloud Eureka 客户端 注册到 Eureka 服务器
eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:9090/eureka

# 配置客户端应用关联的应用
# spring.cloud.config.name 是可选的
# 如果没有配置，采用 ${spring.application.name}
spring:
  cloud:
    config:
      name: nec-config
      profile: pro        # 关联 profile
      label: master       # 关联 label
      discovery:
        enabled: true     # 激活 Config 服务器发现
        service-id: spring-cloud-config-server      # 配置 Config 服务器的应用名称（Service ID）
```

##### 再次检验效果

访问 http://localhost:8080/env：

```json

"configService:https://github.com/AbelEthan/spring-cloud-config-repo/neo-config-pro.yml":{
    "name": "neo-config-pro.com",
    "nc.user.id": 7,
    "nc.user.name": "spring-client",
    "nc.user.age": 22
},
"configService:https://github.com/AbelEthan/spring-cloud-config-repo/neo-config.yml":{
    "name": "neo-config-pro.com"
}
```

以上内容来自于`spring-cloud-config-server`:

http://localhost:7070/neo-config-pro.yml

```yaml
name: neo-config-pro.com
nc:
  user:
    age: 22
    id: 7
    name: spring-client
```



