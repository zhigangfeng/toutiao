package com.newcoder.service;

import com.newcoder.util.JedisAdapter;
import com.newcoder.util.RedisLikeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/2/14.
 */
@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;
    public int getLikeStatus(int userId,int entityType,int entityId){
        String likeKey= RedisLikeUtil.getLikeKey(entityId,entityType);
        if(jedisAdapter.sismember(likeKey,String.valueOf(userId))){
            return 1;
        }
        String disLikeKey=RedisLikeUtil.getDisikeKey(entityId,entityType);
        return jedisAdapter.sismember(disLikeKey,String.valueOf(userId))? -1:0;
    }

    public long like(int userId,int entityType,int entityId ){
        String likeKey= RedisLikeUtil.getLikeKey(entityId,entityType);
        jedisAdapter.sadd(likeKey,String.valueOf(userId));
        String dislikeKey=RedisLikeUtil.getDisikeKey(entityId,entityType);
        jedisAdapter.srem(dislikeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }

    public long dislike(int userId,int entityType,int entityId ){
        String dislikeKey= RedisLikeUtil.getDisikeKey(entityId,entityType);
        jedisAdapter.sadd(dislikeKey,String.valueOf(userId));
        String likeKey=RedisLikeUtil.getLikeKey(entityId,entityType);
        jedisAdapter.srem(likeKey,String.valueOf(userId));
        return jedisAdapter.scard(likeKey);//返回给前端的始终是喜欢的数量
    }
}

