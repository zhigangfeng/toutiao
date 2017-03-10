package com.newcoder.configuration;

import com.newcoder.intercepter.LoginRequireInterceptor;
import com.newcoder.intercepter.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Administrator on 2017/1/3.
 */
//配置拦截器
@Component
public class ToutiaoConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    PassportInterceptor passportInterceptor;
    @Autowired
    LoginRequireInterceptor loginRequireInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(passportInterceptor);
    registry.addInterceptor(loginRequireInterceptor).addPathPatterns("/setting*");
    super.addInterceptors(registry);
}
}
