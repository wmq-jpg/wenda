package com.example.wenda.controller;

import com.example.wenda.aspect.LogAspect;
import com.example.wenda.model.*;
import com.example.wenda.service.CommentService;
import com.example.wenda.service.FollowService;
import com.example.wenda.service.QuestionService;
import com.example.wenda.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger( HomeController.class);
    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    FollowService followService;

    @RequestMapping(path={"/user/{userId}"},method=RequestMethod.GET)
    public String userId(Model model, @PathVariable("userId")int userId)
    {
        model.addAttribute("vos",getQuestions(userId,0,10));
        User user=userService.getUser(userId);
        ViewObject vo=new ViewObject();
        vo.set("user",user);
        vo.set("commentCount",commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount( EntityType.ENTITY_USER,userId));
        if(hostHolder.getUser()!=null)
        {
            vo.set("followed",followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_USER,userId));
        }
        else
        {
            vo.set("followed",false);
        }
        model.addAttribute("profileUser",vo);
        return "profile";
    }

    @RequestMapping(path={"/","/index"},method= RequestMethod.GET)
    public String index(Model model)
    {
        model.addAttribute("vos",getQuestions(0,0,10));
        return "index";
    }
    private List<ViewObject> getQuestions(int userId,int offset,int limit)
    {   List<Question> questionList=questionService.getLatestQuestions(userId,offset,limit);
        List<ViewObject>vos=new ArrayList<ViewObject>();
        for(Question question:questionList)
        {
            ViewObject vo=new ViewObject();
            vo.set("question",question);
            vo.set("user",userService.getUser(question.getUserId()));
            vo.set("followCount",followService.getFollowerCount(EntityType.ENTITY_QUESTION,question.getId()));
            vos.add(vo);
        }
       return vos;
    }
}
