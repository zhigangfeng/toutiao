package com.newcoder.async.handler;


import com.newcoder.async.EventHandler;
import com.newcoder.async.EventModel;
import com.newcoder.async.EventType;
import com.newcoder.model.Message;
import com.newcoder.model.User;
import com.newcoder.service.MessageService;
import com.newcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/2/27.
 */
@Component
//通知站内信
public class LikeHandler implements EventHandler {
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;
    @Override
    public void doHandle(EventModel model) {
        Message message=new Message();
        User user=userService.getUser(model.getActorId());
        //message.setToId(model.getEntityOwnerId());
        message.setToId(model.getActorId());
        message.setContent("用户"+user.getName()+"赞了你的资讯"+String.valueOf(model.getEntityId()));
        //System.out.println("liked");
        message.setFromId(3);
        message.setCreatedDate(new Date());
        message.setConversationId("1");
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
