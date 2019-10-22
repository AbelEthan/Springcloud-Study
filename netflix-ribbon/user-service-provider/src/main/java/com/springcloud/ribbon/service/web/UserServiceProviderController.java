package com.springcloud.ribbon.service.web;

import com.springcloud.ribbon.api.UserService;
import com.springcloud.ribbon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Title:
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/23 9:32
 */
@RestController
public class UserServiceProviderController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/save")
    public boolean user(@RequestBody User user){
        return userService.saveUser(user);
    }

}
