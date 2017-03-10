package com.newcoder.controller;

import com.newcoder.model.EntityType;
import com.newcoder.model.HostHolder;
import com.newcoder.model.News;
import com.newcoder.model.ViewObject;
import com.newcoder.service.LikeService;
import com.newcoder.service.NewsService;
import com.newcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/24.
 */
@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private LikeService likeService;
    @Autowired
    HostHolder hostHolder;

    private List<ViewObject> getNews(int userId, int offset, int limit){
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        int localUserId=hostHolder.getUser()!=null? hostHolder.getUser().getId():0;
        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            if(localUserId!=0){
                vo.set("like",likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
            }else{
                vo.set("like",0);
            }
            vos.add(vo);
        }
        return vos;
    }
   /* private List<ViewObject> getNews1(String key,long start,long end){
        List<News> newsList=newsService.getSortedNews(key,start,end);
        int localUserId=hostHolder.getUser()!=null? hostHolder.getUser().getId():0;
        List<ViewObject> vos = new ArrayList<>();
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            if(localUserId!=0){
                vo.set("like",likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS,news.getId()));
            }else{
                vo.set("like",0);
            }
            vos.add(vo);
        }
        return vos;
    }*/
    @RequestMapping(path = {"/","/index"},method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value="pop",defaultValue = "0") int pop) {
        List<ViewObject> vos = new ArrayList<>();
        vos=getNews(0,0,10);
        model.addAttribute("vos",vos);
        model.addAttribute("pop",pop);
        return "home";
    }


    @RequestMapping(path = {"/user/{userId}/"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getNews(userId, 0, 10));
        return "home";
    }
}
