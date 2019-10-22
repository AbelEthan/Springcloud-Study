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
