package com.skyzd.spider.dao.domain;

/**
 * Created by sky.chi on 4/16/2017 11:31 PM.
 * Email: sky8chi@gmail.com
 */
public class User {
    private Long userId;
    private String username;
    private String userpass;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", userpass='" + userpass + '\'' +
                '}';
    }
}
