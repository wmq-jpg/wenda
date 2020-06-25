package com.example.wenda.controller;

import com.example.wenda.model.EntityType;
import com.example.wenda.model.Feed;
import com.example.wenda.model.HostHolder;
import com.example.wenda.service.FeedService;
import com.example.wenda.service.FollowService;
import com.example.wenda.service.UserService;
import com.example.wenda.util.JedisAdapter;
import com.example.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;


@Controller
public class FeedController {
    @Autowired
    FeedService feedService;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Autowired
    HostHolder hostHolder;

    //拉模式 从数据库表中读出关注的人的新鲜事 占用内存小
     @RequestMapping(path={"/pullfeeds"},method= RequestMethod.GET)
    private String getPullfeeds(Model model)
    {
        int localUserId=hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
        List<Integer> followees=new ArrayList<>();
        if(localUserId!=0)
        {
             followees=followService.getFollowees(EntityType.ENTITY_USER,localUserId,Integer.MAX_VALUE);
        }
        List<Feed> feeds=feedService.getUserFeeds(Integer.MAX_VALUE,followees,10);
        model.addAttribute("feeds",feeds);
        return "feeds";
    }

    //推模式 将新鲜事推给所有的粉丝，粉丝从自己的新鲜事列表中读取。
    @RequestMapping(path={"/pushfeeds"},method= RequestMethod.GET)
    private String getPushfeeds(Model model)
    {
        int localUserId=hostHolder.getUser()!=null?hostHolder.getUser().getId():0;
         List<String>feedIds=jedisAdapter.lrange(RedisKeyUtil.getTimelineKey(localUserId),0,10);
         List<Feed> feeds=new ArrayList<>();
         for (String feedId:feedIds)
         {
             Feed feed=feedService.getById(Integer.parseInt(feedId));
             if(feed!=null)
                 feeds.add(feed);
         }
         model.addAttribute("feeds",feeds);
        return "feeds";
    }



}
