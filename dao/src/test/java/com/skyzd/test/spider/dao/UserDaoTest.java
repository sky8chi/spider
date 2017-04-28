package com.skyzd.test.spider.dao;

import com.skyzd.spider.dao.UserDao;
import com.skyzd.spider.dao.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by sky.chi on 4/16/2017 11:34 PM.
 * Email: sky8chi@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:/spring/*.xml"})
public class UserDaoTest {
    @Resource
    private UserDao mUserDao;

    @Test
    public void save() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("jack");
        user.setUserpass("1234");
        mUserDao.save(user);
    }

    @Test
    public void findAll() {
        List<User> userList = mUserDao.findAll();
        for (User user : userList) {
            System.out.println(user);
        }
    }
}
