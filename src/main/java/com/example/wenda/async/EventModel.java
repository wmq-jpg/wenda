package com.example.wenda.async;

import java.util.HashMap;
import java.util.Map;

public class EventModel {
    private EventType type;
    private int actorId;
    private int entityType;
    private int entityId;
    private int entityOwnerId;
    private Map<String,String> exts=new HashMap<String,String>();
    public EventModel()
    {
    }
    public EventModel(EventType eventType)
    {
        this.type = eventType;

    }
    public void setType(EventType type) {
        this.type = type;
    }

    public EventModel setActorId(int actorId) {
        this.actorId = actorId;
        return this;
    }

    public  EventModel setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public EventModel setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public EventModel setEntityOwnerId(int entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public EventModel setExts(Map<String, String> exts) {
        this.exts = exts;
        return this;
    }

    public EventType getType() {
        return type;
    }

    public int getActorId() {
        return actorId;
    }

    public int getEntityType() {
        return entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getEntityOwnerId() {
        return entityOwnerId;
    }

    public Map<String, String> getExts() {
        return exts;
    }
    public EventModel setExt(String key,String value)
    {
        exts.put(key,value);
        return this;
    }
    public String getExt(String key)
    {
       return  exts.get(key);
    }
}
