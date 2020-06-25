package com.example.wenda.async.handler;


import com.example.wenda.async.EventHandler;
import com.example.wenda.async.EventModel;
import com.example.wenda.async.EventType;
import com.example.wenda.model.Message;
import com.example.wenda.model.User;
import com.example.wenda.service.MessageService;
import com.example.wenda.service.UserService;
import com.example.wenda.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class LikeHandler  implements EventHandler {
    @Autowired
    UserService userService;
    @Autowired
    MessageService messageService;

    @Override
    public void doHandle(EventModel model) {
        Message message=new Message();
        message.setToId(model.getEntityOwnerId());
        message.setCreatedDate(new Date());
        message.setFromId(WendaUtil.SYSTEM_USERID);
        User user=userService.getUser(model.getActorId());
        message.setContent("用户"+user.getName()+"赞了你的评论http:127.0.0.1:8889:/question"+model.getExt("questionId"));
        messageService.addMessage(message); //对应的站内信被放入数据库等待被显示

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
