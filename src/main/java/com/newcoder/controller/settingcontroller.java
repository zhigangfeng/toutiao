package com.newcoder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2016/12/21.
 */
@Controller
public class settingcontroller {
    @RequestMapping("/setting")
    @ResponseBody
    public String setting(){
        return "Setting is OK!";
    }
}
