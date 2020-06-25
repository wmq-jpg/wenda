package com.example.wenda.dao;

import com.example.wenda.model.Comment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;
@Mapper
@Component(value = "CommentDAO")
public interface CommentDAO {



        String TABLE_NAME = "comment";
        String INSERT_FIELDS = "user_id,entity_id,entity_type,content,status,created_date";
        String SELECT_FIELDS = "id," + INSERT_FIELDS;

        @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{userId},#{entityId},#{entityType},#{content},#{status},#{createdDate})"})
        int addComment(Comment comment);

        @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where entity_id=#{entityId} and entity_type=#{entityType} order by created_date desc"})
        List<Comment> selectCommentByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId);

        @Select({"select count(id) from ",TABLE_NAME,"where entity_type=#{entityType} and entity_id=#{entityId}"})
        int getCommentCount(@Param("entityType") int entityType,@Param("entityId") int entityId);

        @Update({"update",TABLE_NAME,"status=#{status} where id=#id}"})
        int updateStatus(@Param("id")int id,@Param("status")int status);


        @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,"where id=#{id}"})
        Comment selectCommentById(int id);

        @Select({"select  count(id) from",TABLE_NAME, "where user_id=#{userId}"})
                int  getUserCommentCount(int userId);






}



