package com.newcoder.controller;

import com.newcoder.async.EventModel;
import com.newcoder.async.EventProducer;
import com.newcoder.async.EventType;
import com.newcoder.model.EntityType;
import com.newcoder.model.HostHolder;
import com.newcoder.model.News;
import com.newcoder.service.LikeService;
import com.newcoder.service.NewsService;
import com.newcoder.util.JedisAdapter;
import com.newcoder.util.NewsScoreUtil;
import com.newcoder.util.RedisLikeUtil;
import com.newcoder.util.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/2/14.
 */
@Controller
public class LikeController {
    @Autowired
    LikeService likeService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    NewsService newsService;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    JedisAdapter jedisAdapter;
    @RequestMapping(path = {"/like"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String like(@Param("newsId") int newsId){
        long likeCount=likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS,newsId);
        News news=newsService.getById(newsId);
        jedisAdapter.zadd("newsRank", NewsScoreUtil.newsScore((int)likeCount, (int)jedisAdapter.scard(RedisLikeUtil.getDisikeKey(newsId, EntityType.ENTITY_NEWS)), news.getCreatedDate(),news.getCommentCount())
        ,String.valueOf(newsId));
        newsService.updateLikeCount(newsId,(int)likeCount);

        eventProducer.fireEvent(new EventModel(EventType.LIKE).setEntityOwnerId(news.getUserId())
                .setActorId(hostHolder.getUser().getId()).setEntityId(newsId));
        return ToutiaoUtil.getJSONString(0,String.valueOf(likeCount));
    }

    @RequestMapping(path = {"/dislike"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String dislike(@Param("newsId") int newsId){
        long likeCount=likeService.dislike(hostHolder.getUser().getId(), EntityType.ENTITY_NEWS,newsId);
        jedisAdapter.zadd("newsRank", NewsScoreUtil.newsScore((int)likeCount, (int)jedisAdapter.scard(RedisLikeUtil.getDisikeKey(newsId, EntityType.ENTITY_NEWS)),newsService.getById(newsId).getCreatedDate(),newsService.getById(newsId).getCommentCount())
                ,String.valueOf(newsId));
        newsService.updateLikeCount(newsId,(int)likeCount);
        return ToutiaoUtil.getJSONString(0,String.valueOf(likeCount));
    }
}
