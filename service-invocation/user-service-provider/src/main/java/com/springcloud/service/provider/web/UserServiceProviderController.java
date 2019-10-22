package com.springcloud.service.provider.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.springcloud.service.api.UserService;
import com.springcloud.service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Title: 用户服务提供方 Controller
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/23 9:32
 */
@RestController
public class UserServiceProviderController implements UserService{

    @Autowired
    @Qualifier("inMemoryUserService")
    private UserService userService;

    private final static Random random = new Random();

    @Override
    public boolean saveUser(@RequestBody User user){
        return userService.saveUser(user);
    }

    @HystrixCommand(
            // Command 配置
            commandProperties = {
                    // 设置操作时间为 100 毫秒
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "100")
            },
            fallbackMethod = "fallbackForGetUsers"
    )
    @Override
    public List<User> findAll() {
        return userService.findAll();
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
