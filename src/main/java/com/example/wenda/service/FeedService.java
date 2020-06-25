package com.example.wenda.service;

import com.example.wenda.dao.FeedDAO;
import com.example.wenda.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    @Autowired
    FeedDAO feedDAO;
//拉模式
    public List<Feed> getUserFeeds(int maxId,List<Integer>userIds,int count)
    {
       return  feedDAO.selectUserFeeds(maxId,userIds,count);
    }
    //推
    public boolean addFeed(Feed feed)
    {
        feedDAO.addFeed(feed);
        return feed.getId()>0;
    }
    //查
    public Feed getById(int id)
    {
        return feedDAO.getFeedById(id);
    }

}
