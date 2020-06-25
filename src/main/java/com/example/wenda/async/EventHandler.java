package com.example.wenda.async;

import java.util.List;

public interface EventHandler {


    //处理事件的方法，不同的处理器处理方式不同
    void doHandle(EventModel model);
    //关注事件类型，由该事件处理器处理
    List<EventType> getSupportEventTypes();
}
