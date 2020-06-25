package com.example.wenda.service;

import com.example.wenda.dao.LoginTicketDAO;
import com.example.wenda.dao.UserDAO;
import com.example.wenda.model.LoginTicket;
import com.example.wenda.model.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;
    @Autowired
    LoginTicketDAO loginTicketDAO;
    public Map<String,Object> register(String username,String password)
    {
        Map<String,Object> map=new HashMap<String,Object>();
        if(StringUtils.isBlank(username))
        {
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password))
        {
            map.put("msg","密码不能为空");
            return map;
        }
       User user=userDAO.selectByName(username);
        if(user!=null)
        {
            map.put("msg","该用户已存在");
            return map;
        }
        user=new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png)",new Random().nextInt(1000)));
        user.setPassword(com.example.wenda.util.WendaUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);
        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }
    public Map<String,Object> login (String username,String password)
    {
        Map<String,Object> map=new HashMap<>();
        if(StringUtils.isBlank(username))
        {
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password))
        {
            map.put("msg","密码不能为空");
            return map;
        }
        User user=userDAO.selectByName(username);
        if(user==null)
        {
            map.put("msg","该用户不存在");
            return map;
        }
       if(!com.example.wenda.util.WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword()))
       {
           map.put("msg","密码错误");
           return map;
       }
        String ticket=addLoginTicket(user.getId());
        map.put("ticket",ticket);
        map.put("userId",user.getId());
        return map;
    }
    public  String addLoginTicket(int userId)
    {
            LoginTicket loginTicket=new LoginTicket();
            loginTicket.setUserId(userId);
            Date now=new Date();
            now.setTime(now.getTime()+3600*24*100);
            loginTicket.setExpired(now);
            loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
            loginTicketDAO.addTicket(loginTicket);
            loginTicket.setStatus(0);
            return loginTicket.getTicket();


    }
    public void logout(String ticket)
    {
        loginTicketDAO.updateStatus(ticket,1);
    }
    public User getUser(int id)
    {
        return userDAO.selectById(id);
    }
    public User selectByName(String name)
    {
        return userDAO.selectByName(name);
    }

}
