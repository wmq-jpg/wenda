package com.example.wenda.controller;


import com.example.wenda.async.EventModel;
import com.example.wenda.async.EventProducer;
import com.example.wenda.async.EventType;
import com.example.wenda.model.*;
import com.example.wenda.service.CommentService;
import com.example.wenda.service.FollowService;
import com.example.wenda.service.QuestionService;
import com.example.wenda.service.UserService;
import com.example.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController .class);
    @Autowired
    UserService userService;
    @Autowired
    FollowService followService;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    EventProducer eventProducer;
    @RequestMapping(path={"/followUser"},method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId)
    {
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
         boolean ret=followService.follow(hostHolder.getUser().getId(),userId,EntityType.ENTITY_USER);
         //触发关注人的事件
         eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
         .setEntityOwnerId(userId).setEntityType(EntityType.ENTITY_USER).setEntityId(userId));
         //返回关注的人数
         return WendaUtil.getJSONString(ret?0:1,String.valueOf(followService.getFolloweeCount(EntityType.ENTITY_USER,hostHolder.getUser().getId())));
    }
    @RequestMapping(path={"/unfollowUser"},method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId)
    {
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        boolean ret=followService.unfollow(hostHolder.getUser().getId(),userId,EntityType.ENTITY_USER);
        //触发取消关注的事件
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW).setActorId(hostHolder.getUser().getId())
                .setEntityOwnerId(userId).setEntityType(EntityType.ENTITY_USER).setEntityId(userId));
        //返回关注的人数
        return WendaUtil.getJSONString(ret?0:1,String.valueOf(followService.getFolloweeCount(EntityType.ENTITY_USER,hostHolder.getUser().getId())));
    }

    @RequestMapping(path={"/followQuestion"},method={RequestMethod.POST})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId)
    {
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        Question q=questionService.selectById(questionId);
        if(q==null)
        {
            return WendaUtil.getJSONString(1,"问题不存在");
        }
        boolean ret=followService.follow(hostHolder.getUser().getId(),questionId,EntityType.ENTITY_QUESTION);
        //触发关注问题的事件
        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
                .setEntityOwnerId(q.getUserId()).setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId));
        Map<String,Object>info=new HashMap<>();
        info.put("headUrl",hostHolder.getUser().getHeadUrl());
        info.put("name",hostHolder.getUser().getName());
        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return WendaUtil.getJSONString(ret?0:1,info);

    }
    @RequestMapping(path={"/unfollowQuestion"},method={RequestMethod.POST})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId)
    {
        if(hostHolder.getUser()==null)
            return WendaUtil.getJSONString(999);
        Question q=questionService.selectById(questionId);
        if(q==null)
        {
            return WendaUtil.getJSONString(1,"问题不存在");
        }
        boolean ret=followService.unfollow(hostHolder.getUser().getId(),questionId,EntityType.ENTITY_QUESTION);
        //触发取消关注人的事件
        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW).setActorId(hostHolder.getUser().getId())
                .setEntityOwnerId(q.getUserId()).setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId));
        Map<String,Object>info=new HashMap<>();

        info.put("id",hostHolder.getUser().getId());
        info.put("count",followService.getFollowerCount(EntityType.ENTITY_QUESTION,questionId));
        return WendaUtil.getJSONString(ret?0:1,info);
    }
    @RequestMapping(path={"/user/{uid}/followers"},method={RequestMethod.GET})
    public String followers(@PathVariable("uid")int userId, Model model)
    {
        List<Integer> followerIds=followService.getFollowers(EntityType.ENTITY_USER,userId,10,0);
        if(hostHolder.getUser()!=null)
        {
            model.addAttribute("followers",getUsersInfo(hostHolder.getUser().getId(),followerIds));
        }
        else
        {
            model.addAttribute("followers",getUsersInfo(0,followerIds));
        }
        model.addAttribute("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
        model.addAttribute("curUser",userService.getUser(userId));


        return "followers";
    }

    @RequestMapping(path = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> followeeIds = followService.getFollowees( EntityType.ENTITY_USER,userId, 10, 0);

        if (hostHolder.getUser() != null) {
            model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
        } else {
            model.addAttribute("followees", getUsersInfo(0, followeeIds));
        }
        model.addAttribute("followeeCount", followService.getFolloweeCount( EntityType.ENTITY_USER,userId));
        model.addAttribute("curUser", userService.getUser(userId));
        return "followees";
    }
    public List<ViewObject> getUsersInfo(int localUserId,List<Integer>userIds)
    {

        List<ViewObject> userInfos=new ArrayList<ViewObject>() ;

        for(Integer userId:userIds)
        {   User user=userService.getUser(userId);
           if(user==null)
               continue;
            ViewObject vo=new ViewObject();
            vo.set("user",user);
            vo.set("commentCount",commentService.getUserCommentCount(userId));
            vo.set("followerCount",followService.getFollowerCount(EntityType.ENTITY_USER,userId));
            vo.set("followeeCount",followService.getFolloweeCount(EntityType.ENTITY_USER,userId));
            if(localUserId!=0)
                vo.set("followed",followService.isFollower(localUserId,EntityType.ENTITY_USER,userId));
            else
                vo.set("followed",false);
            userInfos.add(vo);
        }
        return userInfos;
    }





}
