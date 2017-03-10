package com.newcoder.async.handler;

import com.newcoder.async.EventHandler;
import com.newcoder.async.EventModel;
import com.newcoder.async.EventType;
import com.newcoder.model.Message;
import com.newcoder.service.MessageService;
import com.newcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by Administrator on 2017/2/28.
 */
//发邮件
@Component
public class LoginHandler implements EventHandler{
    @Autowired
    MessageService messageService;

    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        Message message=new Message();
        message.setConversationId("1");
        message.setToId(model.getActorId());
        message.setCreatedDate(new Date());
        message.setFromId(3);
        message.setContent("Handler处理开始时间为："+String.valueOf(new Date()));
        messageService.addMessage(message);

        Map<String,Object> map=new HashMap<>();
        map.put("username",model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("to"), "登录提示",
                "mails/welcome.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
