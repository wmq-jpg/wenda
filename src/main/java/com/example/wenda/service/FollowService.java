package com.example.wenda.service;


import com.example.wenda.util.JedisAdapter;
import com.example.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class FollowService {
    @Autowired
    JedisAdapter jedisAdapter;
    /**
     * 用户关注了某个实体,可以关注问题,关注用户,关注评论等任何实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean follow(int userId,int entityId,int entityType)
    {
        String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        Date date=new Date();
        //实体的粉丝增加当前用户
        Jedis jedis=jedisAdapter.getJedis();
        Transaction tx=jedisAdapter.multi(jedis);
        tx.zadd(followerKey,date.getTime(),String.valueOf(userId));
        //当前用户对实体的关注加1
        tx.zadd(followeeKey,date.getTime(),String.valueOf(entityId));
        List<Object> ret=jedisAdapter.exec(tx,jedis);
        return ret.size()==2&&(Long)ret.get(0)>0&&(Long)ret.get(1)>0;
    }
/**
 * 取消关注
 * @param userId
 * @param entityType
 * @param entityId
 * @return
 */
public boolean unfollow(int userId,int entityId,int entityType)
{
    String followerKey= RedisKeyUtil.getFollowerKey(entityType,entityId);
    String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
    Date date=new Date();
    //实体的粉丝减少当前用户
    Jedis jedis=jedisAdapter.getJedis();
    Transaction tx=jedisAdapter.multi(jedis);
    tx.zrem(followerKey,String.valueOf(userId));
    //当前用户对实体的关注加1
    tx.zrem(followeeKey,String.valueOf(entityId));
    List<Object> ret=jedisAdapter.exec(tx,jedis);
    return ret.size()==2&&(Long)ret.get(0)>0&&(Long)ret.get(1)>0;
}
public  List<Integer> getIdFromSet(Set<String> idSet)
{
    List<Integer>ids=new ArrayList<>();
    for(String id:idSet)
    {
        ids.add(Integer.parseInt(id));
    }
    return ids;
}
 public List<Integer> getFollowers(int entityType,int entityId,int count)
 {
     String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
     return getIdFromSet(jedisAdapter.zrevrange(followerKey,0,count));
 }
 public List<Integer> getFollowers(int entityType,int entityId,int count,int offset)
    {
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);

        return getIdFromSet(jedisAdapter.zrevrange(followerKey,offset,count+offset));
    }
    public List<Integer> getFollowees(int entityType,int userId,int count)
    {
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);

        return getIdFromSet(jedisAdapter.zrevrange(followeeKey,0,count));
    }
    public List<Integer> getFollowees(int entityType,int userId,int count,int offset)
    {
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return getIdFromSet(jedisAdapter.zrevrange(followeeKey,offset,count+offset));
    }

    public long getFollowerCount(int entityType,int entityId)
    {
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zcard(followerKey);

    }
    public long getFolloweeCount(int entityType,int userId)
    {
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return jedisAdapter.zcard(followeeKey);

    }

    /**
     *  判断用户是否关注了某个实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean isFollower(int userId,int entityType,int entityId)
    {
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return jedisAdapter.zscore(followerKey,String.valueOf(userId))!=null;
    }




}
