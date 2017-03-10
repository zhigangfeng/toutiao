package com.newcoder.util;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2017/2/13.
 */
@Component
public class JedisAdapter implements InitializingBean {
    //演示Jedis代码片段
    public static void print(int index, Object obj) {
        System.out.println(String.format("%d,%s", index, obj.toString()));
    }

    public static void main(String[] args) {
        Jedis jedis = new Jedis();
        jedis.flushAll();
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        jedis.setex("hello2", 15, "world");//键值对设置有效时间
        jedis.set("pv", "100");
        jedis.incr("pv");
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5);
        print(2, jedis.get("pv"));

        //列表操作
        String listName = "listA";
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(3, jedis.lrange(listName, 0, 12));
        print(4, jedis.llen(listName));
        print(5, jedis.lpop(listName));
        print(6, jedis.llen(listName));
        print(7, jedis.lindex(listName, 3));
        print(8, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a2", "xx"));//返回插入位置的索引
        print(9, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a2", "xxx"));
        print(10, jedis.lrange(listName, 0, 13));

        //hash操作
        String userKey = "user12";
        jedis.hset(userKey, "name", "Jack");
        jedis.hset(userKey, "age", "23");
        jedis.hset(userKey, "sex", "male");
        print(11, jedis.hget(userKey, "sex"));
        print(12, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "sex");
        print(13, jedis.hgetAll(userKey));
        print(14, jedis.hkeys(userKey));
        print(15, jedis.hvals(userKey));
        print(16, jedis.hexists(userKey, "mail"));
        print(17, jedis.hexists(userKey, "age"));
        jedis.hsetnx(userKey, "name", "Jack");
        jedis.hsetnx(userKey, "name", "Jim");//设置hash的一个字段，只有字段不存在时有效
        print(18, jedis.hget(userKey, "name"));
        print(19, jedis.hgetAll(userKey));

        //集合操作
        String likeKeys1 = "newsLike1";
        String likeKeys2 = "newsLike2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKeys1, String.valueOf(i));
            jedis.sadd(likeKeys2, String.valueOf(i * 2));
        }
        print(20, jedis.smembers(likeKeys1));//打印集合中的元素
        print(21, jedis.smembers(likeKeys2));
        print(22, jedis.sinter(likeKeys1, likeKeys2));//交
        print(23, jedis.sunion(likeKeys1, likeKeys2));//并
        print(24, jedis.sdiff(likeKeys1, likeKeys2));//差
        print(25, jedis.sismember(likeKeys1, "4"));//判断集合中是否含有这种元素
        jedis.srem(likeKeys1, "5");
        print(26, jedis.smembers(likeKeys1));
        print(27, jedis.scard(likeKeys1));//集合元素个数
        jedis.smove(likeKeys2, likeKeys1, "14");//集合之间元素移动
        print(28, jedis.scard(likeKeys1));
        print(29, jedis.smembers(likeKeys1));

        //有序集合
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "Jim");
        jedis.zadd(rankKey, 60, "Ben");
        jedis.zadd(rankKey, 90, "Lee");
        jedis.zadd(rankKey, 80, "Mei");
        jedis.zadd(rankKey, 75, "Lucy");
        print(30, jedis.zcard(rankKey));
        print(31, jedis.zcount(rankKey, 61, 100));
        print(32, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Lucy");
        print(33, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "luc");//集合成员添加increment，如果key中不存在这个成员，添加这个成员，score值是increment
        print(34, jedis.zcount(rankKey, 0, 10));
        print(35, jedis.zrange(rankKey, 1, 3));
        print(36, jedis.zrevrange(rankKey, 1, 3));
        for (Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, 0, 100)) {
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }
        print(38, jedis.zrank(rankKey, "Ben"));
        print(39, jedis.zrevrank(rankKey, "Ben"));

        //连接池
        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; i++) {
            Jedis j = pool.getResource();
            j.get("a");
            System.out.println("POOL" + i);
            //j.close();
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost", 6379);
    }

    public void set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String get(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("发生错误" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long zadd(String key, double score,String member) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zadd(key, score,member);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return false;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public long scard(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Set<String> zrerange(String key, long start, long end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key,start,end);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public void setObject(String key, Object object) {
        set(key, JSON.toJSONString(object));
    }

    public <T> T getObject(String key, Class<T> clazz) {
        String value = get(key);
        if (value != null) {
            return JSON.parseObject(value, clazz);
        }
        return null;
    }

    public long lpush(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("发生异常" + e.getMessage());
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
