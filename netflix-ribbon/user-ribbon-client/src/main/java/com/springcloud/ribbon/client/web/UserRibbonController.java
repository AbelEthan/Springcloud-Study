package com.springcloud.ribbon.client.web;

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
