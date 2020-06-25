package com.example.wenda.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.wenda.controller.CommentController;
import com.example.wenda.model.User;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class JedisAdapter implements  InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;
    public static void print(int index, Object obj) {
        System.out.println(String.format("%d,%s", index, obj.toString()));

    }
/*
 public static void main(String[] args) {

        //Jedis jedis = new Jedis("redis://120.26.176.203:6379/9");
        Jedis jedis = new Jedis("120.26.176.203",6379);
        jedis.auth("123");
        jedis.flushDB();
        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(1, jedis.get("newhello"));
        jedis.setex("hello2", 15, "world");
        jedis.set("pv", "100");
        jedis.incr("pv");
        print(2, jedis.get("pv"));
        jedis.incrBy("pv", 5);
        print(2, jedis.get("pv"));
        jedis.decrBy("pv", 3);
        print(2, jedis.get("pv"));
        print(2, jedis.keys("*"));

        //list
        String listname = "list";
        jedis.del(listname);
        for (int i = 0; i < 10; i++) {
            jedis.lpush(listname, "a" + String.valueOf(i));
        }
        print(4, jedis.lrange(listname, 0, 12));
        print(4, jedis.lrange(listname, 0, 3));
        print(5, jedis.llen(listname));
        print(6, jedis.lpop(listname));
        print(7, jedis.llen(listname));
        print(8, jedis.lrange(listname, 2, 6));
        print(9, jedis.lindex(listname, 5));
        print(10, jedis.linsert(listname, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
        print(10, jedis.linsert(listname, BinaryClient.LIST_POSITION.BEFORE, "a4", "bb"));
        print(11, jedis.lrange(listname, 0, 12));
        //hash
        String userKey = "userxx";
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "15150596363");
        print(12, jedis.hget(userKey, "name"));
        print(13, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey));
        print(15, jedis.hexists(userKey, "email"));
        print(16, jedis.hexists(userKey, "age"));
        print(17, jedis.hkeys(userKey));
        print(18, jedis.hvals(userKey));
        jedis.hsetnx(userKey, "school", "zju");
        jedis.hsetnx(userKey, "name", "wmq");
        print(19, jedis.hgetAll(userKey));


        //set
        String likeKey1 = "commentLike1";
        String likeKey2 = "commentLike2";
        for (int i = 0; i < 10; i++) {
            jedis.sadd(likeKey1, String.valueOf(i));
            jedis.sadd(likeKey2, String.valueOf(i * i));
        }
        print(20, jedis.smembers(likeKey1));
        print(21, jedis.smembers(likeKey2));
        print(22, jedis.sunion(likeKey1, likeKey2));
        print(23, jedis.sdiff(likeKey1, likeKey2));
        print(24, jedis.sinter(likeKey1, likeKey2));
        print(25, jedis.sismember(likeKey2, "12"));
        print(26, jedis.sismember(likeKey2, "16"));
        jedis.srem(likeKey1, "5");
        print(27, jedis.smembers(likeKey1));
        jedis.smove(likeKey2, likeKey1, "25");
        print(28, jedis.smembers(likeKey1));
        print(29, jedis.scard(likeKey1));
        String randKey = "randKey";
        jedis.zadd(randKey, 15, "jim");
        jedis.zadd(randKey, 60, "Ben");
        jedis.zadd(randKey, 90, "Lee");
        jedis.zadd(randKey, 75, "Lucky");
        jedis.zadd(randKey, 80, "Mei");
        print(30, jedis.zcard(randKey));
        print(31, jedis.zcount(randKey, 61, 100));
        print(32, jedis.zscore(randKey, "Lucky"));
        jedis.zincrby(randKey, 2, "Lucky");
        print(33, jedis.zscore(randKey, "Lucky"));
        jedis.zincrby(randKey, 2, "Luc");
        print(34, jedis.zscore(randKey, "Luc"));
        print(35, jedis.zrange(randKey, 0, 100));
        print(36, jedis.zrange(randKey, 1, 3));
        print(37, jedis.zrevrange(randKey, 1, 3));
        for (Tuple tuple : jedis.zrangeByScoreWithScores(randKey, "60", "100")) {
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));
        }
        print(38, jedis.zrank(randKey, "Ben"));
        print(39, jedis.zrevrank(randKey, "Ben"));
        String setKey = "zset";
        jedis.zadd(setKey, 1, "a");
        jedis.zadd(setKey, 1, "b");
        jedis.zadd(setKey, 1, "c");
        jedis.zadd(setKey, 1, "d");
        jedis.zadd(setKey, 1, "e");
        print(40, jedis.zlexcount(setKey, "-", "+"));
        print(41, jedis.zlexcount(setKey, "[b", "[d"));
        print(41, jedis.zlexcount(setKey, "(b", "[d"));
        jedis.zrem(setKey, "b");
        print(43, jedis.zrange(setKey, 0, 10));
        jedis.zremrangeByLex(setKey, "(c", "+");
        print(44, jedis.zrange(setKey, 0, 2));


       JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(200);
        config.setMaxWaitMillis(10000);
        config.setTestOnBorrow(true);
        JedisPool jedisPool = new JedisPool("redis://localhost:6379/9");
      for (int i = 0; i < 100; ++i) {
            Jedis j = jedisPool.getResource();
          print(45, j.get("pv"));
            j.close();
        }




       User user=new User();
       user.setName("xx");
       user.setPassword("xx");
       user.setHeadUrl("a.png");
       user.setSalt("salt");
       user.setId(1);
       print(46, JSONObject.toJSONString(user));
       jedis.set("user1", JSONObject.toJSONString(user));
       String value=jedis.get("user1");
       User user2= JSON.parseObject(value,User.class);
       print(47,user2);

       jedis.close();
        }

*/
//Redis服务器IP
private static String IP = "120.26.176.203";

    //Redis的端口号
    private static int PORT = 6379;

    //Redis服务密码
    private static String password = "123";

    //可用连接实例的最大数目，默认值为8；
    //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
    private static int MAX_ACTIVE = 64;

    //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。
    private static int MAX_IDLE = 20;

    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int MAX_WAIT = 3000;

    private static int TIMEOUT = 3000;

    //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
    private static boolean TEST_ON_BORROW = true;
    private static boolean TEST_ON_RETURN = true;


    @Override
    public void afterPropertiesSet() throws Exception {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(MAX_ACTIVE);
        config.setMaxIdle(MAX_IDLE);
        config.setMaxWaitMillis(MAX_WAIT);
        config.setTestOnBorrow(TEST_ON_BORROW);
        config.setTestOnReturn(TEST_ON_RETURN);

        pool = new JedisPool(config, IP , PORT , TIMEOUT,password);

   //  pool=new JedisPool("120.26.176.203",6379);
        //pool = new JedisPool("redis://loclhost:6379/9");


    }
    public long sadd(String key,String value)
    {
        Jedis jedis=null;
        try
        {
            jedis=pool.getResource();
            return jedis.sadd(key,value);
        }catch(Exception e)
        {
            logger.error("发生异常",e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return 0;

    }
    public long srem(String key,String value)
    {
        Jedis jedis=null;
        try
        {
            jedis=pool.getResource();
            return jedis.srem(key,value);
        }catch(Exception e)
        {
            logger.error("发生异常",e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return 0;

    }
    public long scard(String key)
    {
        Jedis jedis=null;
        try
        {
            jedis=pool.getResource();
            return jedis.scard(key);
        }catch(Exception e)
        {
            logger.error("发生异常",e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return 0;

    }
    public boolean sismember(String key,String value)
    {
        Jedis jedis=null;
        try
        {
            jedis=pool.getResource();
            return jedis.sismember(key,value);
        }catch(Exception e)
        {
            logger.error("发生异常",e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return false;

    }

    public long lpush(String key,String value)
    {
        Jedis jedis=null;
        try
        {
            jedis=pool.getResource();
            return jedis.lpush(key,value);
        }catch(Exception e)
        {
            logger.error("发生异常",e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return 0;

    }
    public List<String> lrange(String key, int start, int end)
    {
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.lrange(key,start,end);
        }
        catch(Exception e)
        {
            logger.error("发生异常"+e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return null;
    }
    public List<String> brpop(int timeout,String key)
    {
        Jedis jedis=null;
        try
        {
            jedis=pool.getResource();
           return jedis.brpop(timeout,key);
        }catch(Exception e)
        {
            logger.error("发生异常",e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return null;

    }
    public long zadd(String key,double score,String value)
    {
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
             return jedis.zadd(key, score, value);
        }
        catch(Exception e)
        {
            logger.error("发生异常"+e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return 0;
    }
    public long zrem(String key,double score,String value)
    {
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.zrem(key, value);
        }
        catch(Exception e)
        {
            logger.error("发生异常"+e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return 0;
    }
    public Jedis getJedis()
    {
        return pool.getResource();
    }
    public Transaction multi(Jedis jedis)
    {
        try{
          return jedis.multi();
        }
        catch(Exception e)
        {
            logger.error("发生异常"+e.getMessage());
        }
        finally
        {

        }
        return null;
    }
    public List<Object> exec(Transaction tx,Jedis jedis)
    {
        try{
            return tx.exec();
        }
        catch(Exception e)
        {
            logger.error("发生异常"+e.getMessage());
        }
        finally
        {
            if(tx!=null)
                try{
                    tx.close();
                }catch(IOException ioe)
                {

                }
        }
        return null;
    }
    public Set<String> zrange(String key, int start, int end)
    {
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.zrange(key,start,end);
        }
        catch(Exception e)
        {
            logger.error("发生异常"+e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return null;
    }
    public Set<String> zrevrange(String key, int start, int end)
    {
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.zrevrange(key,start,end);
        }
        catch(Exception e)
        {
            logger.error("发生异常"+e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return null;
    }
    public long zcard(String key)
    {
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.zcard(key);
        }
        catch(Exception e)
        {
            logger.error("发生异常"+e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return 0;
    }
    public Double  zscore(String key,String member)
    {
        Jedis jedis=null;
        try{
            jedis=pool.getResource();
            return jedis.zscore(key,member);
        }
        catch(Exception e)
        {
            logger.error("发生异常"+e.getMessage());
        }
        finally
        {
            if(jedis!=null)
            {
                jedis.close();
            }
        }
        return null;
    }






}







