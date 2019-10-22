package com.springcloud.load.web.controller;

import com.springcloud.load.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Title: Client
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/18 17:25
 */
@RestController
public class ClientController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service-provider.host}")
    private String serviceProviderHost;

    @Value("${service-provider.port}")
    private String serviceProvidePort;

    @Value("${service-provider.name}")
    private String serviceProvideName;


    @GetMapping("")
    public String index(){
        User user = new User();
        user.setId(1001L);
        user.setName("AbelEthan");
        return restTemplate.postForObject("http://" + serviceProvideName + "/greeting",user,String.class);
    }
}
