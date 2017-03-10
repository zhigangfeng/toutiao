package com.newcoder.service;

import com.newcoder.dao.CommentDAO;
import com.newcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/1/12.
 */
@Service
public class CommentService {
    @Autowired
    private CommentDAO commentDAO;
    public List<Comment> getCommentByEntity(int entityId,int entityType){
        return commentDAO.selectByEntity(entityId,entityType);
    }
    public int addComment(Comment comment ){return commentDAO.addComment(comment);}

    public int getCommentCount(int entityId,int entityType){
        return commentDAO.getComment(entityId,entityType);
    }
}
