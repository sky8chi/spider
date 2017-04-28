package com.skyzd.spider.dao;

import com.skyzd.spider.dao.domain.User;

import java.util.List;

/**
 * Created by sky.chi on 4/16/2017 11:32 PM.
 * Email: sky8chi@gmail.com
 */
public interface UserDao {
    public List<User> findAll();

    public void save(User user);
}
