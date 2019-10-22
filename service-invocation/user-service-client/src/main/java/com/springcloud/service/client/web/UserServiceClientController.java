package com.springcloud.service.client.web;

import com.springcloud.service.api.UserService;
import com.springcloud.service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Title: {@link UserServiceClientController} 客户端 {@link RestController}
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/11/2 11:17
 */
@RestController
public class UserServiceClientController implements UserService {

    @Autowired
    private UserService userService;

    // 通过方法继承，URL 映射 ："/user/save"
    @Override
    public boolean saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // 通过方法继承，URL 映射 ："/user/find/all"
    @Override
    public List<User> findAll() {
        return userService.findAll();
    }
}
