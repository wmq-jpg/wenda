package com.example.wenda.model;

import java.util.Date;

public class LoginTicket {
    private int id;
    private int userId;
    private Date expired;
    private int status;
    private String ticket;

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public Date getExpired() {
        return expired;
    }

    public int getStatus() {
        return status;
    }

    public String getTicket() {
        return ticket;
    }
}
