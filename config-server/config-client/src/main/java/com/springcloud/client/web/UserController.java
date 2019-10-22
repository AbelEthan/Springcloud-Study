package com.springcloud.client.web;

import com.springcloud.client.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Title:
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/6 16:02
 */
@RestController
@EnableConfigurationProperties(User.class)
public class UserController {

    /**
     *  通过构造器注入
     */
    private final User user;

    @Autowired
    public UserController(User user) {
        this.user = user;
    }

    @GetMapping("/user")
    public User getUser() {
        return user;
    }
}
