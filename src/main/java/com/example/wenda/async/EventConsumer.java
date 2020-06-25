package com.example.wenda.async;


import com.alibaba.fastjson.JSON;

import com.example.wenda.util.JedisAdapter;

import com.example.wenda.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventConsumer implements InitializingBean , ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    //映射触发事件的所有handler
    private Map<EventType, List<EventHandler>> config=new HashMap<>();
    public ApplicationContext applicationContext;////继承ApplicationContextAware类，用于获取所有注入的handler
    @Autowired
    JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
         //获取实现EventHandler的所有类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if (beans != null) {
            //遍历每个事件处理器
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                //得到每个事件处理器的支持处理事件的类型
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
               //遍历每个事件
                for (EventType type : eventTypes) {
                    //如果映射集合中不包含该事件类型，在集合中创建该事件类型
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    //然后在事件处理器中加入到事件对应的事件处理器链表中
                    config.get(type).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) { //在多线程中持续的监听队列等待处理事件
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0, key); //线程一直阻塞直到有事件发生

                    for (String message : events) {
                        if (message.equals(key)) {   //Redis自带消息key要过滤掉
                            continue;
                        }

                        EventModel eventModel = JSON.parseObject(message, EventModel.class);  //将字符串转化为对象
                        if (!config.containsKey(eventModel.getType())) {
                            logger.error("不能识别的事件");
                            continue;
                        }
                         //该事件对应的事件处理器去处理该事件
                        for (EventHandler handler : config.get(eventModel.getType())) {
                            handler.doHandle(eventModel);
                        }
                    }
                }
            }
        });
        thread.start();
    }




    @Override
    public void setApplicationContext(org.springframework.context.ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
