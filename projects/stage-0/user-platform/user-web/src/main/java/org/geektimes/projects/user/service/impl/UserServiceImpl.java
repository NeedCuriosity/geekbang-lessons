package org.geektimes.projects.user.service.impl;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.service.UserService;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @author zhouzy
 * @since 2021-03-02
 */
public class UserServiceImpl implements UserService {

    @Resource(name = "bean/DatabaseUserRepository")
    private DatabaseUserRepository userRepository;

    @Override
    public boolean register(User user) {
        return userRepository.save(user);
    }

    @PreDestroy
    public void destory() {
        System.out.println("关闭UserService");
    }

    @Override
    public boolean deregister(User user) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User queryUserById(Long id) {
        return null;
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return null;
    }
}
