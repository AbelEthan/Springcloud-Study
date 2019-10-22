## Java 微服务实践 - [Spring Cloud 系列笔记](https://github.com/AbelEthan/spring-cloud)

Spring Cloud 为开发人员提供快速构建分布式系统的一些通用模式，其中包括：配置管理、服务发现、服务短路、智能路由、微型网关、控制总线、一次性令牌、全局锁、领导选举、分布式会话和集群状态。分布式系统间的协调导向样板模式，并且使用 Spring Cloud 的开发人员能够快速地构建实现这些模式的服务和应用。这些服务和应用也将在任何环境下工作良好，无论是开发者的笔记本、还是数据中心裸机或者管控平台。

### 课程特点

Spring Cloud 系列致力于以实战的方式覆盖 Spring Cloud 的功能特性，结合 Spring Cloud 的源码加深理解，最终达到形成系统性的知识和技术体系的目的。

### [云原生应用（Cloud Native Applications）]() 

* 系列介绍：介绍 Spring Cloud 系列课程内容、讲述方式以及整体技术栈，大体上分为：技术核心理念、使用方法和经验、以及源码分析三大类
* 储备力量：介绍深入系统化学习 Spring Cloud 所预备的理论和技术储备。架构方面，如：面向服务架构（SOA）、微服务架构、事件驱动架构。技术方面，如：Java 8、Spring Framework、Spring Boot，以及搭建云应用的运行环境基本要素
* 理解 12-Factor 应用：介绍 12-Factor 应用，并且结合技术体系，如传统 Java EE、 Spring Framework 以及 Spring Boot 
* Bootstrap 应用上下文：介绍 Spring Cloud 新引入的 Bootstrap 应用上下文，说明其与 Spring Framework 应用上下文之间的联系，进一步理解 Bootstrap 应用上下文在 Spring Boot 应用中的层次关系
* 端点介绍：介绍 Spring Cloud 在 Spring Boot 基础上新引入的端点（Endpoint），比如：上下文重启:`/restart`、生命周期：`/pause`、`/resume`等

### [配置客户端（Spring Cloud Config Client）]()

* 技术回顾：回顾提及的 Environment、以及 Spring Boot 配置相关的事件和监听器，如`ApplicationEnvironmentPreparedEvent`和`ConfigFileApplicationListener`，
* Bootstrap 配置属性：解密 Bootstrap 配置属性与 Spring Framework / Spring Boot 配置架构的关系，介绍如何调整 Bootstrap 配置文件路径、覆盖远程配置属性、自定义 Bootstrap 配置以及自定义 Bootstrap 配置属性源
* Environment 端点：介绍`/env` 端点的使用场景，并且解读其源码，了解其中奥秘
* 安全：介绍客户端配置安全相关议题

### [配置服务器（Spring Cloud Config Server）](https://github.com/AbelEthan/spring-cloud/tree/master/config-server) 

* 基本使用：介绍`@EnableConfigServer`、`Environment` 仓储、秘钥管理等基本使用方法
* 分布式配置官方实现：介绍 Spring 官方标准分布式配置实现方式：Git实现 和 文件系统实现
* 动态配置属性 Bean：介绍`@RefreshScope`基本用法和使用场景，并且说明其中的局限性
* 健康指标：介绍 Spring Boot 标准端口（`/health`）以及 健康指标（Health Indicator）
* 分布式配置自定义实现：基于配置管理容器 Zookeeper ，自定义实现分布式配置能力
* 健康指标自定义实现：实现分布式配置的健康指标自定义实现

### [服务发现/注册（Service Discovery/Registry）](https://github.com/AbelEthan/spring-cloud/tree/master/service-discovery)

* 前微服务时代：介绍前微服务时代，服务发现和注册在 SOA 甚至是更早的时代的技术实现和实施方法，如 WebService 中的UDDI、REST 中的 HEATOAS
* 高可用架构：简介高可用架构的基本原则，计算方法和系统设计
* Eureka 客户端：介绍 Spring Cloud Discovery 结合 Netflix Eureka 客户端的基本使用方法，包括服务发现激活、Eureka 客户端注册配置 以及 API 使用等
* Eureka 服务器：介绍 Eureka 服务器作为服务注册中心的搭建方法，以及內建 Dashboard 基本运维手段

### [高可用服务治理（HA Service Government）](https://github.com/AbelEthan/spring-cloud/tree/master/service-governance)


* Eureka 高可用集群：搭建 Eureka 高可用集群，消除服务注册中心单点失效的问题
* Eureka 异地化集群：搭建 Eureka 异地化集群，实现不同区域/地区之间的服务状态同步
* Consul 高可用集群：相对于 Eureka 作为服务注册中心，Consul 提供更加的状态一致性保证，通过搭建 Consul  高可用服务治理集群

### [负载均衡（Load Balance）](https://github.com/AbelEthan/spring-cloud/tree/master/load-leveling)

* 理论基础：简介负载均衡客户端和服务端的相关理论，包括调度算法：如先来先服务、轮训、多级队列等。基本特性：非对称负载、健康检查、优先级队列等
* 技术回顾：回顾 Spring Framework HTTP 组件 RestTemplate 的使用方法，结合 ClientHttpRequestInterceptor 实现简单负载均衡客户端
* 整合Netflix Ribbon：作为 Spring Cloud 客户端负载均衡实现 ，Netflix Ribbon 提供了丰富的组件，包括负载均衡器、负载均衡规则、PING 策略等，根据前章所积累的经验，实现客户端负载均衡

### [Netflix Ribbon 源码解读](https://github.com/AbelEthan/spring-cloud/tree/master/netflix-ribbon)

* RestTemplate 部分：解读 RestTemplateCustomizer 设计意图、处理逻辑以及请求拦截实现
* 客户端部分：解读`@LoadBalanced` 实现原理以及最终实际请求的相关实现
* 负载均衡部分：解读负载均衡上下文、负载均衡器、负载均衡规则、PING 策略等相关实现

### [服务短路（Circuit Breakers）](https://github.com/AbelEthan/spring-cloud/tree/master/service-circuit)

* 核心理念：介绍服务短路的名词由来、目的，以及相关的类似慨念。随后讲述其中设计哲学、触发条件、处理手段以及客户端和服务端实现方法
* Spring Cloud Hystrix：作为服务端服务短路实现，介绍 Spring Cloud Hystrix 常用限流的功能，同时，说明健康指标以及数据指标在生产环境下的现实意义
* 生产准备特性：介绍聚合数据指标 Turbine 、Turbine Stream，以及整合 Hystrix Dashboard

### [Spring Cloud Hystrix 源码解读](https://github.com/AbelEthan/spring-cloud/tree/master/service-hystrix)

* RxJava：回顾传统观察者模式、反应器模式等设计模式，说明这些模式与 Reactive 模式的联系，更好地解读 Reactive Java 框架相关实现源码，如：`Observer`、`Subscriber`等
* Netflix Hystrix：Netflix Hystrix 作为 Spring Cloud Hystrix 底层实现，结合 RxJava 框架进行实现，如核心接口`HystrixCommand`，属性设置`HystrixCommandProperties`以及配置信息`HystrixConfiguration`等
* Spring Cloud Hystrix：详细解读`@EnableCircuitBreaker` 处理过程，以及`@HystrixCommand` AOP 拦截实现等

### [服务调用（Service Call）](https://github.com/AbelEthan/spring-cloud/tree/master/service-invocation)

* 核心理念：回顾远程服务调用（RPC）的核心理念，介绍接口定义语言（IDL）以及服务存根（Stubs）以及通讯协议，如二进制协议 RMI、文本协议 REST 等
* Spring Cloud Feign ：介绍声明式客户端REST实现 Spring Cloud Feign的使用方式（如`@EnableFeignClients` 、 `@FeignClient`)，结合 Eureka 构建分布式服务应用
* 整合支持：Spring Cloud Feign 整合 Hystrix 以及 Ribbon

### [Spring Cloud 服务网关]()

* 核心概念：介绍服务网关使用场景、服务能力、依赖关系、架构以及类型
* 服务端实现：介绍 Spring Cloud Zuul 使用场景、依赖管理、生命周期、源码分析、设计模式
* 整合支持：解读 Hystrix 以及 Ribbon

### [消息驱动整合]()

  * Spring Cloud Stream 
  * Kafka 绑定实现
  * RabbitMQ 绑定实现

### [Spring Cloud Stream Binder 实现]()

* Spring Cloud Stream Binder：实现 ActiveMQ Spring Cloud Stream Binder

### [消息总线]()

* 面向消息服务器
* Kafka 实现
* RabbitMQ 实现 

### [分布式应用跟踪]() 
* 分布式应用跟踪
* ZipKin 整合

### [Spring Cloud 系列回顾]()
