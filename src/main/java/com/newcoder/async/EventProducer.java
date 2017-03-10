package com.newcoder.async;

import com.alibaba.fastjson.JSONObject;
import com.newcoder.util.JedisAdapter;
import com.newcoder.util.RedisLikeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/2/27.
 */
@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel){
        try{
            String json= JSONObject.toJSONString(eventModel);
            String key= RedisLikeUtil.getEventQueueKey();
            jedisAdapter.lpush(key,json);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
