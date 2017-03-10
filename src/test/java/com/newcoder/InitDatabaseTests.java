package com.newcoder;

import com.newcoder.dao.CommentDAO;
import com.newcoder.dao.LoginTicketDAO;
import com.newcoder.dao.NewsDAO;
import com.newcoder.dao.UserDAO;
import com.newcoder.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

/**
 * Created by Administrator on 2016/12/23.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {
    @Autowired
    UserDAO userDAO;
    @Autowired
    NewsDAO newsDAO;
    @Autowired
    LoginTicketDAO loginTicketDAO;
    @Autowired
    CommentDAO commentDAO;
    @Test
    public void contextLoads() {
        Random r = new Random();
        for (int i = 0; i < 11; ++i) {
            News news = new News();
            User user = new User();
            user.setName(String.format("USER%d", i));
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", r.nextInt(1000)));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);

            news.setCommentCount(3);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
            news.setCreatedDate(date);
            news.setImage(String.format("http://images.nowcoder.com/head/%dm.png", r.nextInt(1000)));
            news.setLikeCount(i + 1);
            news.setLink("http://www.seu.edu.cn");
            news.setTitle(String.format("Title {%d} ", i));
            news.setUserId(i+1);
            newsDAO.addNews(news);
            user.setPassword("newpassword");
            //每个新闻插入3条评论
            for(int j=0;j<3;j++){
                Comment comment=new Comment();
                comment.setUserId(i+1);
                comment.setCreatedDate(new Date());
                comment.setStatus(0);
                comment.setEntityId(news.getId());
                comment.setContent("评论"+String.valueOf(j));
                comment.setEntityType(EntityType.ENTITY_NEWS);
                commentDAO.addComment(comment);
            }


            LoginTicket ticket = new LoginTicket();
            ticket.setStatus(0);
            ticket.setUserId(i+1);
            ticket.setExpired(date);
            ticket.setTicket(String.format("TICKET%d", i+1));
            loginTicketDAO.addTicket(ticket);
            //loginTicketDAO.updateStatus(ticket.getTicket(), 2);
        }

    }
}
