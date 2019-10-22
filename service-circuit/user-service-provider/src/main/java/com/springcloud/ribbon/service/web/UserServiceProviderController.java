package com.springcloud.ribbon.service.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springcloud.ribbon.api.UserService;
import com.springcloud.ribbon.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Title:
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/23 9:32
 */
@RequestMapping("/user")
@RestController
public class UserServiceProviderController {

    @Autowired
    private UserService userService;

    private final static Random random = new Random();

    @PostMapping("/save")
    public boolean saveUser(@RequestBody User user){
        return userService.saveUser(user);
    }

    /**
     * 获取所有用户列表
     *
     * @return
     */
    @HystrixCommand(
            // Command 配置
            commandProperties = {
                    // 设置操作时间为 100 毫秒
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100")
            },
            fallbackMethod = "fallbackForGetUsers"
    )
    @GetMapping("/list")
    public List<User> getUsers() throws InterruptedException {
        int executeTime = random.nextInt(200);
        //通过休眠来模拟执行时间
        System.out.println("Execute Time : " + executeTime + " ms");
        Thread.sleep(executeTime);
        return userService.findAll();
    }

    /**
     * {@link #getUsers()} 的 fallback 方法
     *
     * @return 空集合
     */
    public List<User> fallbackForGetUsers(){
        return Collections.emptyList();
    }

}
