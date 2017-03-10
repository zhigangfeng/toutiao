package com.newcoder.service;


import com.newcoder.dao.LoginTicketDAO;
import com.newcoder.dao.UserDAO;
import com.newcoder.model.LoginTicket;
import com.newcoder.model.User;
import com.newcoder.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Administrator on 2016/12/23.
 */
@Service
public class UserService {
    @Autowired
    UserDAO userDAO;
    @Autowired
    LoginTicketDAO loginTicketDAO;
    public User getUser(int id){
        return userDAO.selectById(id);
    }
    //用户注册成功后，添加用户记录
    public Map<String,Object> register(String name,String password){
       Map<String,Object> map=new HashMap<>();
       //验证用户名
       if (StringUtils.isBlank(name)) {
            map.put("msgname","用户名不能为空");
            return map;
        }
        //验证密码
        if (StringUtils.isBlank(password)) {
            map.put("msgpwd","密码不能为空");
            return map;
        }
        //验证用户名是否已经存在
        User user=userDAO.selectByName(name);
        if(user!=null){
            map.put("msgname","用户名已经被注册");
            return map;
        }
        //数据库中插入注册用户记录
        user=new User();
        user.setName(name);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));//？？？？你拿用户名加盐？
        userDAO.addUser(user);
        //注册成功就下发一个ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        return map;
        }
    //用户登录
    public Map<String,Object> login(String name,String password){
        Map<String,Object> map=new HashMap<>();
        //验证用户名
        if (StringUtils.isBlank(name)) {
            map.put("msgname","用户名不能为空");
            return map;
        }
        //验证密码
        if (StringUtils.isBlank(password)) {
            map.put("msgpassword","密码不能为空");
            return map;
        }
        User user=userDAO.selectByName(name);
        //用户尚未注册
        if(user==null){
            map.put("msguser","用户尚未注册");
            return map;
        }
        //验证密码
        if(!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msgpassword","密码错误");
            return map;
        }

        map.put("userId", user.getId());
        //添加ticket
        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }
    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    //用户登出
    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }
}
