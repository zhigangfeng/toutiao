package com.newcoder.controller;

import com.newcoder.async.EventModel;
import com.newcoder.async.EventProducer;
import com.newcoder.async.EventType;
import com.newcoder.service.UserService;
import com.newcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/29.
 */
@Controller
public class LoginController {
    private static final Logger logger= LoggerFactory.getLogger(LoginController.class);//添加日志类，记录有关信息
    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;
    //注册处理
    @RequestMapping(path={"/reg/"},method ={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String reg(Model model,@RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value="rember", defaultValue = "0") int rememberme,
                      HttpServletResponse response){
        try{
            Map<String,Object> map=userService.register(username,password);
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme > 0) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);
                return ToutiaoUtil.getJSONString(0, "注册成功");
            }else {
                return ToutiaoUtil.getJSONString(1, map);
            }
        }catch (Exception e){
            logger.error("注册异常"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"注册异常");
        }
    }
    //登录处理
    @RequestMapping(path={"/login/"},method ={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String login(Model model,@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value="rember", defaultValue = "0") int rememberme,
                        HttpServletResponse response){
        try{
            Map<String,Object> map=userService.login(username,password);
            //如果登录成功，向用户cookie中写入ticket
            if(map.containsKey("ticket")){
                Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme>0){
                    cookie.setMaxAge(3600*24*5);
                    //cookie.setMaxAge(-1);//此时cookie只在浏览器打开时有效
                }
                response.addCookie(cookie);
                eventProducer.fireEvent(new EventModel(EventType.LOGIN).setActorId((int) map.get("userId"))
                        .setExt("username", "牛客").setExt("to", "1320643326@qq.com"));
                return ToutiaoUtil.getJSONString(0,"登录成功");
            }else{
                return ToutiaoUtil.getJSONString(1,map);
            }
        }catch (Exception e){
            logger.error("登录异常"+e.getMessage());
            return ToutiaoUtil.getJSONString(1, "登录异常");
        }
    }
    //登出处理
    @RequestMapping(path = {"/logout/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket){//参数绑定注解。@CookieValue 可以把Request header中关于cookie的值绑定到方法的参数上
        userService.logout(ticket);
        return "redirect:/";//登出以后自动跳到首页
    }

}

