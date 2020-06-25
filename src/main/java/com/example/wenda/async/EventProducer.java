package com.example.wenda.async;


import com.alibaba.fastjson.JSONObject;
import com.example.wenda.util.JedisAdapter;
import com.example.wenda.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {
    @Autowired
    JedisAdapter jedisAdapter;
    public boolean fireEvent(EventModel eventModel)
    {
        try{
            String jason= JSONObject.toJSONString(eventModel);
            String key= RedisKeyUtil.getEventQueueKey();
            jedisAdapter.lpush(key,jason);

            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }




}
