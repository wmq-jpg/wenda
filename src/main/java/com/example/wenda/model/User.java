package com.example.wenda.model;

public class User {
    private String name;
    private int id;
    private String password;
    private String salt;
    private String headUrl;

    public int getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getSalt() {
        return salt;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public User()
    {

    }
    public User(String name)
    {
      this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getDescription()
    {
        return "This is"+name;
    }
}
