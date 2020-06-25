package com.example.wenda.model;

import java.util.Date;

public class Comment {
    private int id;
    private int userId;
    private int entityId;
    private int entityType;
    private Date createdDate;
    private String content;
    private int status;

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getEntityType() {
        return entityType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getContent() {
        return content;
    }

    public int getStatus() {
        return status;
    }
}
