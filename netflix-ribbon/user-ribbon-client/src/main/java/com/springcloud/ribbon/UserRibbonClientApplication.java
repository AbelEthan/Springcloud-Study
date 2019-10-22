package com.springcloud.ribbon;

import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.springcloud.ribbon.client.ping.ClientPing;
import com.springcloud.ribbon.client.rule.ClientRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;

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
     * 将 {@link ClientPing} 暴露成 {@link Bean}
     *
     * @return {@link ClientPing}
     */
    @Bean
    public IPing myPing(){
        return new ClientPing();
    }
}
