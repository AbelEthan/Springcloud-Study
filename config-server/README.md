# Spring Cloud 配置服务器

## 搭建 Spring Cloud Config Server

### 基于文件系统（File System）

#### 创建本地仓库

1. 激活应用配置服务器
    
    在引导类伤标注`@EnableConfigServer`
  
    ```java
      package com.springcloud.config;
      
      import org.springframework.boot.SpringApplication;
      import org.springframework.boot.autoconfigure.SpringBootApplication;
      import org.springframework.cloud.config.server.EnableConfigServer;
      
      /**
       * Config Demo class
       *
       * @author abelethan
       * @date 2018/9/29
       */
      @EnableConfigServer
      @SpringBootApplication
      public class ConfigApplication {
      
          public static void main(String[] args) {
              SpringApplication.run(ConfigApplication.class, args);
          }
      }
    ```
  
2. 创建本地目录

    > 理解 Java 中的 ${user.dir}，在 IDE 中是指定当前项目物理路径：
    
    在 IDEA 中`src/main/resources`目录下，创建一个名为"configs"，它的绝对路径：

    `${user.dir}/src/main/resources/config`

3. 配置 git 本地仓库 URI

   ```properties
     ## 配置服务器文件系统git 仓库
       ## ${user.dir} 减少平台文件系统的不一致
       spring.cloud.config.server.git.uri = ${user.dir}/src/main/resources/configs
   ```
   
4. 给应用"neo-config"创建三个环境的配置文件

   ```
   -rw-r--r--  1 neo-config-pro.yml
   -rw-r--r--  1 neo-config-test.yml
   -rw-r--r--  1 neo-config.yml
   ```
   
   三个文件的环境profile分别（从上至下）是：`prod`、`test`、`default`

5. 初始化本地 git 仓库

    ```
    git init
    git add .
    git commit -m "First commit"
    [master (root-commit) 9bd81bd] First commit
        3 files changed, 9 insertions(+)
        create mode 100644 neo-config-pro.yml
        create mode 100644 neo-config-test.yml
        create mode 100644 neo-config.yml
    ```

#### 测试配置服务器

通过浏览器测试应用为"neo-config"，Profile为："test"的配置内容 : http://127.0.0.1:8080/neo-config/test

```json
{
  "name": "neo-config",
  "profiles":[
    "test"
  ],
  "label": null,
  "version": "ab06ad9977bfd4f4eebbcec5845368ab4c2a0737",
  "state": null,
  "propertySources":[
    {
      "name": "https://github.com/AbelEthan/spring-cloud-config-repo/neo-config-test.yml",
      "source":{
        "name": "neo-config-test"
       }
    }
  ]
}
```

请注意：当指定了profile 时，默认的 profile（不指定）配置信息也会输出：

## 基于远程 Git 仓库

1. 激活应用配置服务器

    在引导类上标注`@EnableConfigServer`

2. 配置远程 Git 仓库地址

   ```properties
   ## 配置服务器远程 Git 仓库（GitHub）
   spring.cloud.config.server.git.uri = https://github.com/AbelEthan/spring-cloud-config-repo
   ```

3. 本地 clone 远程Git 仓库

   ```
   git clone https://github.com/https://github.com/AbelEthan/spring-cloud-config-repo.git
   ```
   
   ```
   $ ls -als
   total 24
   0 drwxr-xr-x   6 Mercy  staff  192 11  3 21:16 .
   0 drwx------+ 12 Mercy  staff  384 11  3 21:16 ..
   0 drwxr-xr-x  12 Mercy  staff  384 11  3 21:16 .git
   8 -rw-r--r--   1 Mercy  staff   40 11  3 21:16 README.md
   ```

4. 给应用"neo-config"创建三个环境的配置文件

   ```
   -rw-r--r--   1 neo-config-pro.yml
   -rw-r--r--   1 neo-config-test.yml
   -rw-r--r--   1 neo-config.yml
   ```

5. 提交到 远程 Git 仓库

   ```
   $ git add .
   $ git commit -m "Add neo-config config files"
   [master 297989f] Add neo-config config files
    3 files changed, 9 insertions(+)
    create mode 100644 neo-config-prod.yml
    create mode 100644 neo-config-test.yml
    create mode 100644 neo-config.yml
   $ git push
   Counting objects: 5, done.
   Delta compression using up to 8 threads.
   Compressing objects: 100% (5/5), done.
   Writing objects: 100% (5/5), 630 bytes | 630.00 KiB/s, done.
   Total 5 (delta 0), reused 0 (delta 0)
   To https://github.com/mercyblitz/tmp.git
      d2b742b..297989f  master -> master
   ```

6. 配置强制拉去内容

    ```properties
    ## 强制拉去 Git 内容
    spring.cloud.config.server.git.force-pull = true
    ```

7. 重启应用
   
## 配置 Spring Cloud 配置客户端    
    
1. 创建 Spring Cloud Config Client 应用

   创建一个名为 `client` 应用

2. ClassPath 下面创建 bootstrap.yml

3. 配置  bootstrap.yml

   配置 以`spring.cloud.config.` 开头配置信息

   ```yaml
   spring:
     cloud:
       config:
         name: neo-config    # 配置客户端应用关联的应用
         profile: pro      # 关联 profile
         label: master      # 关联 label
         uri: http://127.0.0.1:8080/     # 配置应用服务器URI

   ```

   application.yml 信息

   ```yaml
   # 配置服务器应用名称
   spring:
     application:
       name: spring-cloud-config-client
   # 配置服务器端口
   server:
     port: 8020
   # 关闭管理端actuator 的安全
   # /env /health 端口完全开放
   management:
     security:
       enabled: false
   ```

4. 启动应用

```
[main] c.c.c.ConfigServicePropertySourceLocator : Fetching config from server at: http://127.0.0.1:8080/
[main] c.c.c.ConfigServicePropertySourceLocator : Located environment: name=neo-config, profiles=[pro], label=master, version=ab06ad9977bfd4f4eebbcec5845368ab4c2a0737, state=null
[main] b.c.PropertySourceBootstrapConfiguration : Located property source: CompositePropertySource [name='configService', propertySources=[MapPropertySource {name='configClient'}, MapPropertySource {name='https://github.com/AbelEthan/spring-cloud-config-repo/neo-config-pro.yml'}]]
```

### 测试 Spring Cloud 配置客户端

通过浏览器访问 http://localhost:8020/env

```json
"configService:configClient":{
  "config.client.version": "edc9158801d3bb5ea750015979cb744bfa28745c"
},
"configService:https://github.com/AbelEthan/spring-cloud-config-repo/neo-config-pro.yml":{
  "name": "neo-config-pro.com"
},
```



通过具体的配置项`name`: http://localhost:8020/env/name

```json
{
  "name": "neo-config-pro.com"
}
```

## 动态配置属性Bean



#### 定义配置属性Bean `User`

```java
package com.springcloud.client.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Objects;

/**
 * Title: 用户对象
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/6 15:51
 */
@ConfigurationProperties(prefix = "nc.user")
public class User {

    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

```

#### 将 `User` 关联配置项

```pr
## 用户的配置信息
## 用户 ID
nc.user.id = 1
## 用户名称
nc.user.name = springcloud
```

>  通过浏览器访问
>
> * http://localhost:8020/env/sf.user.*
>
> ```json
> {
>   "nc.user.id": "1",
>   "nc.user.name": "springcloud"
> }
> ```
>
> * http://localhost:8020/user
>
> ```json
> {
>   "id": 1,
>   "name": "springcloud"
> }
> ```
>
> 

### 通过 Postman 调整配置项

POST 方法提交参数 nc.user.id = 007 、nc.user.name = xiaofei 到 `/env`

```json
  nc.user.id = 007
  nc.user.name = xiaofei
```

调整后，本地的http://localhost:8020/user 的内容变化:

```json
{
  "id": 7,
  "name": "xiaofei"
}
```

问题，如果`spring-cloud-config-client`需要调整所有机器的配置如何操作？

> 注意，配置客户端应用的所关联的分布式配置内容，优先于传统 `application.yml`（application.properties）或者 `bootstrap.yml`（bootstrap.properties）

#### 调整配置服务器配置信息：`neo-config-pro.yml`
