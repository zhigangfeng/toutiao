package com.newcoder.controller;

import com.newcoder.model.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/20.
 */
//@Controller
public class IndexController {
    @RequestMapping(path = {"/","/index"})
    @ResponseBody
    public String index() {
        return "Today is Tuesday";
    }
    @RequestMapping(path = {"/profile/{groupId}/{userId}"})
    @ResponseBody
    public String file(@PathVariable("groupId") String groupId,
                       @PathVariable("userId") int userId,
                       @RequestParam(value="type", defaultValue="1") int type,
                       @RequestParam(value="key", defaultValue="newcoder") String key){
        return String.format("GID{%s},UID{%d},TYPE{%d},KEY{%s}",groupId,userId,type,key);
    }
    @RequestMapping(path = {"/vm"})
    public String newa(Model model){
        model.addAttribute("value","haha");
        List<String> colors= Arrays.asList(new String[]{"RED","GREEN","BLUE"});
        Map<String,String> map=new HashMap<String,String>();
        for(int i=0;i<10;i++){
            map.put(String.valueOf(i),String.valueOf(i*i));
        }
        model.addAttribute("colors",colors);
        model.addAttribute("map",map);
        model.addAttribute("user",new User("Tom"));//传递自定义类的对象
        return "news";//把变量传递给模板
    }
}
