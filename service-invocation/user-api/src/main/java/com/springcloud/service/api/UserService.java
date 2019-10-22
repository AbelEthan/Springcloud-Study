package com.springcloud.service.api;

import com.springcloud.service.entity.User;
import com.springcloud.service.fallback.UserServiceFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * Title: 用戶服务
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/22 18:30
 */
// 利用占位符避免未来整合硬编码
@FeignClient(name = "${user.service.name}", fallback = UserServiceFallback.class)
public interface UserService {

    /**
     * 保存用户
     * @param user
     * @return
     */
    @PostMapping("/user/save")
    boolean saveUser(User user);

    /**
     * 查询所有的用户列表
     *
     * @return non-null
     */
    @GetMapping("/user/find/all")
    List<User> findAll();
}
