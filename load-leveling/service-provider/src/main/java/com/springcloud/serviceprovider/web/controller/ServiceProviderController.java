package com.springcloud.serviceprovider.web.controller;

import com.springcloud.serviceprovider.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Title: 服务提供方 Controller
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/18 17:12
 */
@RestController
public class ServiceProviderController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${server.port}")
    private Integer port;

    @PostMapping("/greeting")
    public String greeting(@RequestBody User user){
        return "Greeting , " + user + " on port : " + port;
    }
}
