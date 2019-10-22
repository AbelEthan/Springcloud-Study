package com.springcloud.service.fallback;

import com.springcloud.service.api.UserService;
import com.springcloud.service.entity.User;

import java.util.Collections;
import java.util.List;

/**
 * Title: {@link UserServiceFallback} Fallback 实现
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/11/2 11:29
 */
public class UserServiceFallback implements UserService {
    @Override
    public boolean saveUser(User user) {
        return false;
    }

    @Override
    public List<User> findAll() {
        return Collections.emptyList();
    }
}
