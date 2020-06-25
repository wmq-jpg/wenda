package com.example.wenda.controller;

import com.example.wenda.async.EventModel;
import com.example.wenda.async.EventProducer;
import com.example.wenda.async.EventType;
import com.example.wenda.model.Comment;
import com.example.wenda.model.EntityType;
import com.example.wenda.model.HostHolder;
import com.example.wenda.service.CommentService;
import com.example.wenda.service.QuestionService;
import com.example.wenda.service.SensitiveService;
import com.example.wenda.service.UserService;
import com.example.wenda.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

@Controller
public class CommentController {
    @Autowired
    CommentService commentService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    QuestionService questionService;
    @Autowired
    SensitiveService sensitiveService;
    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;
    private static final Logger logger = LoggerFactory.getLogger(CommentController .class);
    @RequestMapping(path={"/addComment"},method ={RequestMethod.POST})
    public  String addComment(@RequestParam("questionId") int questionId,@RequestParam("content")String content)
    {
        try{
            content=HtmlUtils.htmlEscape(content);
            content=sensitiveService.filter(content);
        Comment comment=new Comment();
        comment.setContent(content);
        if(hostHolder.getUser()!=null)
        {
            comment.setUserId(hostHolder.getUser().getId());
        }
        else {
            comment.setUserId(WendaUtil.ANONYMOUS_USERID);
        }
        comment.setContent(content);
        comment.setCreatedDate(new Date());
        comment.setEntityId(questionId);
        comment.setEntityType(EntityType.ENTITY_QUESTION);
        comment.setStatus(0);
        commentService.addComment(comment);
        int count=commentService.getCommentCount(comment.getEntityType(),comment.getEntityId());
        questionService.updateCommentCount(comment.getEntityId(),count);
        //怎么异步化 触发评论事件
            eventProducer.fireEvent(new EventModel(EventType.Comment).setActorId(comment.getUserId())
            .setEntityId(questionId));


        }
        catch(Exception e)
        {
            logger.error("增加评论失败",e.getMessage());
        }
      return "redirect:/question/"+String.valueOf(questionId);
    }









}
