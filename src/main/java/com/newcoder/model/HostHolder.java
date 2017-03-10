package com.newcoder.model;

import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2017/1/2.
 */
@Component
public class HostHolder {
        private static ThreadLocal<User> user=new ThreadLocal<>();
        public User getUser(){
            return user.get();
        }
    public void setUser(User users){
        user.set(users);
    }
    public void clear(){
        user.remove();
    }
}
