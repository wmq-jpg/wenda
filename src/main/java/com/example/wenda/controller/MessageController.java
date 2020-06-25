package com.example.wenda.controller;

import com.example.wenda.model.HostHolder;
import com.example.wenda.model.Message;
import com.example.wenda.model.User;
import com.example.wenda.model.ViewObject;
import com.example.wenda.service.CommentService;
import com.example.wenda.service.MessageService;
import com.example.wenda.service.UserService;
import com.example.wenda.util.WendaUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController .class);
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    @RequestMapping(path={"/msg/detail"},method= {RequestMethod.GET})
    public String  conversationDetail(Model model, @RequestParam("conversationId")String conversationId)
    {
        try{
            List<Message>conversationList=messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages=new ArrayList<>();
            for(Message msg:conversationList)
            {
                ViewObject vo=new ViewObject();
                vo.set("message",msg);
                User user=userService.getUser(msg.getFromId());
                if(user==null)
                    continue;
                vo.set("userId",user.getId());
                vo.set("headUrl",user.getHeadUrl());
                messages.add(vo);
            }

         model.addAttribute("messages",messages);
        }
        catch(Exception e)
        {
            logger.error("获取详情消息失败"+e.getMessage());

        }
        return "letterDetail";
    }


    @RequestMapping(path={"/msg/addMessage"},method= {RequestMethod.POST})
    @ResponseBody
    public String  addMessage(Model model, @RequestParam("toName")String toName,@RequestParam("content") String content)
    {
        try{
            if(hostHolder.getUser()==null)
           return WendaUtil.getJSONString(999,"未登录"); //向前端页面返回未登录的json串
            User user=userService.selectByName(toName);
            if(user==null)
            {
                return WendaUtil.getJSONString(1,"用户不存在");
            }
            Message message=new Message();
            message.setContent(content);
            message.setToId(user.getId());
            message.setFromId(hostHolder.getUser().getId());
            message.setCreatedDate(new Date());
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);
        }
        catch(Exception e)
        {
            logger.error("增加站内信失败"+e.getMessage());
            return WendaUtil.getJSONString(1,"插入站内信失败");
        }

    }


    @RequestMapping(path={"/msg/list"},method= {RequestMethod.GET})
    public String  getConversationList(Model model) {
        try{
            int localUserId=hostHolder.getUser().getId();
            List<Message> conversationList=messageService.getConversationList(localUserId,0,10);
            List<ViewObject> conversations=new ArrayList<ViewObject>();
            for(Message msg:conversationList)
            {
                ViewObject vo=new ViewObject();
                vo.set("message",msg);
                int targetId=msg.getFromId()==localUserId?msg.getToId():msg.getFromId();
              User user=  userService.getUser(targetId);
                vo.set("user",user);
                vo.set("unread",messageService.getConversationCount(localUserId,msg.getConversationId()));
                conversations.add(vo);

            }
            model.addAttribute("conversations",conversations);
        }
        catch(Exception e)
        {
            logger.error("获取站内信失败"+e.getMessage());

        }
        return "letter";
    }
    }

