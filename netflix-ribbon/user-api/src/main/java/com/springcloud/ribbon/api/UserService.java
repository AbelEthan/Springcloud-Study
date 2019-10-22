package com.springcloud.ribbon.api;

import com.springcloud.ribbon.entity.User;

import java.util.List;

/**
 * Title: 用戶服务
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/22 18:30
 */
public interface UserService {

    /**
     * 保存用户
     * @param user
     * @return
     */
    boolean saveUser(User user);

    /**
     * 查询所有的用户列表
     *
     * @return non-null
     */
    List<User> findAll();
}
