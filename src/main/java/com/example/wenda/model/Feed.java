package com.example.wenda.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class Feed {
    private int id;
    private int type;
    private int userId;
    private Date createdDate;
    private String data;
    private JSONObject dataJSON=null;

    public void setId(int id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setData(String data) {
        this.data = data;
        dataJSON=JSONObject.parseObject(data);
    }
    public String  get(String key)
    {
        return  dataJSON==null?null:dataJSON.getString(key);
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getUserId() {
        return userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getData() {
        return data;
    }
}
