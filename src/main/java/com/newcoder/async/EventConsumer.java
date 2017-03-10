package com.newcoder.async;

/**
 * Created by Administrator on 2017/2/27.
 */


import com.alibaba.fastjson.JSON;
import com.newcoder.util.JedisAdapter;
import com.newcoder.util.RedisLikeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean,ApplicationContextAware{
    @Autowired
    JedisAdapter jedisAdapter;
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private Map<EventType,List<EventHandler>> config=new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
         this.applicationContext=applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String,EventHandler> beans=applicationContext.getBeansOfType(EventHandler.class);//获取某一类所有的bean
        if(beans!=null){
            for(Map.Entry<String,EventHandler> entry : beans.entrySet()) {//对于每一个bean
                List<EventType> eventType = entry.getValue().getSupportEventTypes();//获取bean的事件类型
                for (EventType type : eventType) {//对每一个事件类型
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }//如果没有类型，添加类型
                    config.get(type).add(entry.getValue());//添加类型的处理方法
                }
            }
        }
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    String key= RedisLikeUtil.getEventQueueKey();
                    List<String> messages=jedisAdapter.brpop(0,key);
                    for(String message: messages) {
                        if (message.equals(key)) {
                            continue;
                        }
                        EventModel eventModel = JSON.parseObject(message, EventModel.class);
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error(" 不能识别事件");
                            continue;
                        }

                        for(EventHandler handler: config.get(eventModel.getType())){
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }
}
