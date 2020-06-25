package com.example.wenda.controller;

import com.example.wenda.aspect.LogAspect;
import com.example.wenda.async.EventModel;
import com.example.wenda.async.EventProducer;
import com.example.wenda.async.EventType;
import com.example.wenda.model.*;
import com.example.wenda.service.*;
import com.example.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.wenda.util.WendaUtil.ANONYMOUS_USERID;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(  QuestionController.class);
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    @Autowired
    FollowService followService;
    @Autowired
    SearchService searchService;
    @Autowired
    EventProducer eventProducer;
    @RequestMapping(value="/question/add",method={RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title")String title,@RequestParam("content")String content)
    {
        try
        {
            Question question=new Question();
            question.setContent(content);
            question.setTitle(title);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            if(hostHolder.getUser()==null) {
                question.setUserId(ANONYMOUS_USERID);
            }
            else {

                question.setUserId(hostHolder.getUser().getId());
            }

            if(questionService.addQuestion(question) >0) {
                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION)
                .setEntityId(question.getId()).setActorId(question.getUserId()).setExt("title",question.getTitle())
                        .setExt("content",question.getContent()));
                return WendaUtil.getJSONString(0);
            }

        }
        catch(Exception e)
        {
            logger.error("增加题目失败",e.getMessage());
        }
        return WendaUtil.getJSONString(1,"失败");
    }
     @RequestMapping(value="/question/{qid}",method =RequestMethod.GET)
     public String QuestionDetail(@PathVariable("qid")int qid, Model model)
     {
         Question question=questionService.selectById(qid);

         User user=userService.getUser(question.getUserId());
         model.addAttribute("question",question);
         List<Comment> commentList=commentService. getCommentByEntity(EntityType.ENTITY_QUESTION ,qid);
         List<ViewObject> vos=new ArrayList<>();
         for(Comment comment:commentList)
         {
             ViewObject vo=new ViewObject();
             vo.set("comment",comment);
             if(hostHolder.getUser()==null)
                 vo.set("liked",0);
                 else
                     vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),EntityType.ENTITY_COMMENT,comment.getId()));
                 vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
             vo.set("user",userService.getUser(comment.getUserId()));
             vos.add(vo);
         }
         model.addAttribute("comments",vos);
         List<ViewObject> followUsers=new ArrayList<>();
         List<Integer> users=followService.getFollowers(EntityType.ENTITY_QUESTION,qid,20);
         for(Integer userId:users)
         {
             ViewObject vo=new ViewObject();
             User u=userService.getUser(userId);
             if(u==null)
                 continue;
             vo.set("name",u.getName());
             vo.set("headUrl",u.getHeadUrl());
             vo.set("id",u.getId());
             followUsers.add(vo);
         }
         model.addAttribute("followUsers",followUsers);
         if(hostHolder.getUser()!=null)
         {
             model.addAttribute("followed",followService.isFollower(hostHolder.getUser().getId(),EntityType.ENTITY_QUESTION,qid));
         }
         else
         {
             model.addAttribute("followed",false);
         }


          return "detail";


     }


}
