package com.example.wenda.model;

import java.util.Date;

public class Question {
    private int id;
    private String title;
    private String content;
    private Date createdDate;
    private int userId;
    private int commentCount;

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }


    public int getUserId() {
        return userId;
    }

    public int getCommentCount() {
        return commentCount;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

}
