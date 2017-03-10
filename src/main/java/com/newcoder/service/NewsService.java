package com.newcoder.service;

import com.newcoder.dao.NewsDAO;
import com.newcoder.model.News;
import com.newcoder.util.JedisAdapter;
import com.newcoder.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Administrator on 2016/12/24.
 */
@Service
public class NewsService{
    @Autowired
    private NewsDAO newsDAO;
    @Autowired
    JedisAdapter jedisAdapter;

    public List<News> getLatestNews(int userId, int offset, int limit){
        return  newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }

    public List<News> getSortedNews(String key,long start, long end){
        List<News> list=new ArrayList<>();
        Set<String> set=jedisAdapter.zrerange(key,start,end);
        for(String  newsId : set){
            list.add(newsDAO.selectById(Integer.parseInt(newsId)));
        }
        return list;
    }
    public News getById(int newsId){
        return newsDAO.selectById(newsId);
    }
    //保存图片
    public String saveImage(MultipartFile file) throws IOException{
        //获取文件原始名，去掉点分隔符
        int dotPos=file.getOriginalFilename().indexOf(".");
        //如果找不到点分隔符
        if(dotPos<0)
            return null;
        //获取文件名的后缀，统一以小写格式保存
        String fileExt=file.getOriginalFilename().substring(dotPos+1).toLowerCase();
        //判断文件是否为图片格式，如果文件格式不合法，直接返回null
        if(!ToutiaoUtil.isFileAllowed(fileExt)) {
            return null;
        }
        //给文件任意去一个名字
        String fileName= UUID.randomUUID().toString().replace("-","")+"."+fileExt;
        //存储图片
        Files.copy(file.getInputStream(),new File(ToutiaoUtil.IMAGE_DIR+fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        //返回文件地址
        return ToutiaoUtil.IMAGR_DOMAIN+"image?name="+fileName;
    }
    //添加用户
    public int addNews(News news){
        newsDAO.addNews(news);
        return news.getId();
    }
    //更新评论的数量
    public int  updateCommentCount(int id,int commentCount){
        return newsDAO.updateCommentCount(id,commentCount);
    }

    public int updateLikeCount(int id,int likeCount){
        return newsDAO.updateLikeCount(id,likeCount);
    }
}
