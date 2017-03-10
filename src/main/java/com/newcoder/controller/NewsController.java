package com.newcoder.controller;

import com.newcoder.model.*;
import com.newcoder.service.*;
import com.newcoder.util.JedisAdapter;
import com.newcoder.util.NewsScoreUtil;
import com.newcoder.util.RedisLikeUtil;
import com.newcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/4.
 */
//图片上传,存储到服务器中
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
    @Autowired
    private NewsService newsService;
    @Autowired
    private UserService userService;
    @Autowired
    private QiniuService qiniuService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    @Autowired
    JedisAdapter jedisAdapter;
    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String upLoadImage(@RequestParam("file") MultipartFile file){
        try{
            //String fileUrl=newsService.saveImage(file);
            String fileUrl=qiniuService.saveImage(file);
            if(fileUrl==null){
                return ToutiaoUtil.getJSONString(1,"上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0,fileUrl);
        }catch(Exception e){
            logger.error("上传图片失败"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"上传图片失败");
        }
    }
    //图片展示
    @RequestMapping(path = {"/image"},method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName,
                         HttpServletResponse response){
        try{
            //response.setContentType(MIME)的作用是使客户端浏览器，区分不同种类的数据，并根据不同的MIME调用浏览器内不同的程序嵌入模块来处理相应的数据
            response.setContentType("image/png");
            StreamUtils.copy(new FileInputStream(new
                    File(ToutiaoUtil.IMAGE_DIR+imageName)),response.getOutputStream());
        }catch (Exception e){
            logger.error("图片展示错误"+e.getMessage());
        }
    }
    //存储图片，标题和链接
    @RequestMapping(path={"/user/addNews/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image")String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {
            if (hostHolder.getUser() == null) {
                return ToutiaoUtil.getJSONString(0, "请先登录或注册");
            } else {
                News news = new News();
                Date date=new Date();
                news.setCreatedDate(date);
                news.setTitle(title);
                news.setImage(image);
                news.setLink(link);
                news.setUserId(hostHolder.getUser().getId());
                newsService.addNews(news);
                jedisAdapter.zadd("newsRank", NewsScoreUtil.newsScore(0,0,date,0),String.valueOf(news.getId()));
                return ToutiaoUtil.getJSONString(0);
            }
        } catch (Exception e) {
            logger.error("添加资讯失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(0, "发布失败");
        }
    }
    @RequestMapping(path={"/news/{newsId}"},method = {RequestMethod.GET})
    public String newsDetail(@PathVariable("newsId") int newsId, Model model){
        News news=newsService.getById(newsId);
        //添加评论
        if(news!=null){
            int localUserId=hostHolder.getUser()!=null? hostHolder.getUser().getId():0;
            if(localUserId!=0){
                model.addAttribute("like",likeService.getLikeStatus(localUserId,EntityType.ENTITY_NEWS,newsId));
            }else{
                model.addAttribute("like",0);
            }
            List<Comment> comments=commentService.getCommentByEntity(newsId, EntityType.ENTITY_NEWS);
            //将用户信息和评论信息放在一起
            List<ViewObject> commentVOs=new ArrayList<>();
            for(Comment comment : comments){
                ViewObject commentVO=new ViewObject();
                commentVO.set("comment",comment);
                commentVO.set("user",userService.getUser(comment.getUserId()));
                commentVOs.add(commentVO);
            }
            model.addAttribute("comments",commentVOs);
        }
        model.addAttribute("news",news);
        model.addAttribute("owner",userService.getUser(news.getUserId()));
        return "detail";
    }
    //添加评论入口
    @RequestMapping(path={"/addComment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){
        //把用户的评论添加到后台数据库
        Comment comment=new Comment();
        comment.setUserId(hostHolder.getUser().getId());
        comment.setContent(content);
        comment.setEntityType(EntityType.ENTITY_NEWS);
        comment.setEntityId(newsId);
        comment.setCreatedDate(new Date());
        comment.setStatus(0);
        commentService.addComment(comment);
        News news=newsService.getById(newsId);
        //更新commentCount
        int count=commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());//先获取当前评论数量
        newsService.updateCommentCount(newsId,count);
        jedisAdapter.zadd("newsRank", NewsScoreUtil.newsScore(news.getLikeCount(),(int)jedisAdapter.scard(RedisLikeUtil.getDisikeKey(newsId, EntityType.ENTITY_NEWS)),news.getCreatedDate(),news.getCommentCount()),String.valueOf(newsId));

        //数据存入后台以后，返回前面一个页面
        return "redirect:/news/"+String.valueOf(newsId);
    }
}
