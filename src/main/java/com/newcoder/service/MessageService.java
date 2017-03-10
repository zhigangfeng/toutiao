package com.newcoder.service;

import com.newcoder.dao.MessageDAO;
import com.newcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
@Service
public class MessageService {
    @Autowired
    private MessageDAO messageDAO;
    public int addMessage(Message message){
        return messageDAO.addMessage(message);
    }

    public List<Message> getConversationDatail(String conversationId, int offset, int limit){
        return messageDAO.getConversationDetail(conversationId,offset,limit);
    }

    public List<Message> getConversationList(int userId,int offset,int limit){
        return messageDAO.getConversationList(userId,offset,limit);
    }

    public int getUnreadCount(int userId,String conversationId){
        return messageDAO.getConversationUnreadCount(userId,conversationId);
    }
}
