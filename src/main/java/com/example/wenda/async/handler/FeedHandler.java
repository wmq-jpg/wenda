package com.example.wenda.async.handler;


import com.alibaba.fastjson.JSONObject;
import com.example.wenda.async.EventHandler;
import com.example.wenda.async.EventModel;
import com.example.wenda.async.EventType;
import com.example.wenda.model.*;
import com.example.wenda.service.FeedService;
import com.example.wenda.service.FollowService;
import com.example.wenda.service.QuestionService;
import com.example.wenda.service.UserService;
import com.example.wenda.util.JedisAdapter;
import com.example.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class FeedHandler implements EventHandler {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    FeedService feedService;
    @Autowired
    UserService userService;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    QuestionService questionService;
    private String buildFeedData(EventModel model)
    {
        Map<String,String> map=new HashMap<>();
        User actor=userService.getUser(model.getActorId());
        if(actor==null)
            return null;
        map.put("userId",String.valueOf(model.getActorId()));
        map.put("userHead",actor.getHeadUrl());
        map.put("userName",actor.getName());
        //如果发生的事件是某人评论问题事件或者发生的事件是关注问题事件，再把问题的相关信息放入map中
        if(model.getType()==EventType.Comment||
                ( model.getEntityType()==EntityType.ENTITY_QUESTION&& model.getType()==EventType.FOLLOW)) {
            Question question = questionService.selectById(model.getEntityId());
            if(question==null){
                return null;}
            map.put("questionId",String.valueOf(question.getId()));
            map.put("questionTitle",question.getTitle());
            return JSONObject.toJSONString(map);

        }

      return null;
    }
    @Override
    public void doHandle(EventModel model) {

        Feed feed=new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(model.getType().getValue());
        feed.setUserId(model.getActorId());
        feed.setData(buildFeedData(model));//json字符串
        if(feed.getData()==null)
            return ;
        feedService.addFeed(feed);
//获得事件触发者的粉丝列表
        List<Integer>followers=followService.getFollowers(EntityType.ENTITY_USER,model.getActorId(),Integer.MAX_VALUE);
        //系统队列
        followers.add(0);
        //给所有粉丝推事件
        for(int follower:followers)
        {
            String timeline=RedisKeyUtil.getTimelineKey(follower);
            //给每个粉丝的新鲜事队列添加新鲜事的Id

            jedisAdapter.lpush(timeline,String.valueOf(feed.getId()));
        }

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.FOLLOW,EventType.Comment});
    }
}
