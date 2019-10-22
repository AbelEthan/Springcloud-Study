package com.springcloud.ribbon;

import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.springcloud.ribbon.client.ping.ClientPing;
import com.springcloud.ribbon.client.rule.ClientRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
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
@RibbonClient("user-service-provider") // 指定目标应用名称
@EnableCircuitBreaker //使用服务短路
public class UserRibbonClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserRibbonClientApplication.class, args);
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

//    /**
//     * 将 {@link ClientPing} 暴露成 {@link Bean}
//     *
//     * @return {@link ClientPing}
//     */
//    @Bean
//    public IPing myPing(){
//        return new ClientPing();
//    }

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
