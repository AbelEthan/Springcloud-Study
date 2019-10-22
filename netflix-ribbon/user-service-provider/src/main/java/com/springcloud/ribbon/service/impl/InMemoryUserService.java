package com.springcloud.ribbon.service.impl;

import com.springcloud.ribbon.api.UserService;
import com.springcloud.ribbon.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Title: 内存实现 {@link UserService}
 * Description:
 *
 * @author 谭 tmn
 * @email AbelEthan@126.com
 * @date 2018/10/22 18:36
 */
@Service
public class InMemoryUserService implements UserService {

    private Map<Long, User> repository = new ConcurrentHashMap<>();

    @Override
    public boolean saveUser(User user) {
        System.out.println(repository);
        return repository.put(user.getId(), user) == null;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(repository.values());
    }
}
