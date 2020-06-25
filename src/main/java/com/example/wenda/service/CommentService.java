package com.example.wenda.service;


import com.example.wenda.dao.CommentDAO;
import com.example.wenda.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;

    public List<Comment> getCommentByEntity(int entityType,int entityId)
    {
           return commentDAO.selectCommentByEntity(entityType,entityId);
    }
    public int addComment(Comment comment)
    {
        return commentDAO.addComment(comment)>0?comment.getId():0;
    }
    public int   getCommentCount(int entityType,int entityId)
    {
        return commentDAO.getCommentCount(entityType,entityId);
    }
    public boolean DeleteComment(int commentId)
    {
        return commentDAO.updateStatus(commentId,1)>0;
    }
    public Comment getCommentById(int id)
    {
        return commentDAO.selectCommentById(id);
    }
    public int getUserCommentCount(int userId)
    {
        return commentDAO.getUserCommentCount(userId);
    }
}
