package com.newcoder.controller;

import com.newcoder.model.HostHolder;
import com.newcoder.model.Message;
import com.newcoder.model.User;
import com.newcoder.model.ViewObject;
import com.newcoder.service.MessageService;
import com.newcoder.service.UserService;
import com.newcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/1/13.
 */
@Controller
public class MessageController {
    private static final Logger logger=LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @RequestMapping(path={"/msg/addMessage"},method={RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content){
        Message msg=new Message();
        msg.setContent(content);
        msg.setCreatedDate(new Date());
        msg.setToId(toId);
        msg.setFromId(fromId);
        msg.setConversationId(fromId<toId? String.format("%d_%d",fromId,toId):String.format("%d_%d",toId,fromId));
        messageService.addMessage(msg);
        return ToutiaoUtil.getJSONString(msg.getId());
    }

    @RequestMapping(path={"/msg/detail"},method={RequestMethod.GET})
    public String conversationDetail(Model model,@RequestParam("conversationId") String conversationId){
        List<ViewObject> messages=new ArrayList<>();
        List<Message> conversationList=messageService.getConversationDatail(conversationId,0,10);
        for(Message msg: conversationList){
            ViewObject vo=new ViewObject();
            vo.set("message",msg);
            //添加用户信息，为发送者的消息
            User user=userService.getUser(msg.getFromId());
            vo.set("headUrl",user.getHeadUrl());//添加用户头像
            vo.set("userName",user.getName());//添加用户姓名
            messages.add(vo);//最后返回给前端的是messages
        }
        model.addAttribute("messages",messages);
        return "letterDetail";
    }

    @RequestMapping(path={"/msg/list"},method={RequestMethod.GET})
    public String conversationList(Model model){
        int localUserId=hostHolder.getUser().getId();
        List<ViewObject> conversations=new ArrayList<>();
        List<Message> conversationList=messageService.getConversationList(localUserId,0,5);
        for(Message msg: conversationList){
            ViewObject vo=new ViewObject();
            vo.set("conversation",msg);
            int targetId=msg.getFromId()==localUserId? msg.getToId():msg.getFromId();//获取跟我交互对方信息
            User user=userService.getUser(targetId);
            /*vo.set("headUrl",user.getHeadUrl());
            vo.set("userName",user.getName());
            vo.set("targetId",targetId);
            vo.set("totalCount",msg.getId());*/
            vo.set("user", user);
            vo.set("unreadCount",messageService.getUnreadCount(localUserId,msg.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations",conversations);
        return "letter";
    }
}
