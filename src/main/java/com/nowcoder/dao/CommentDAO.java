package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import org.apache.ibatis.annotations.*;
import org.springframework.boot.autoconfigure.jdbc.metadata.CommonsDbcpDataSourcePoolMetadata;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
@Component
public interface CommentDAO{
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = "user_id, content,created_date,entity_id,entity_type,status";
    String SELECT_FIELDS = "id,"+INSERT_FIELDS;

    //增加评论：insert返回值是插入数据的主键
    @Insert({"insert into",TABLE_NAME,"(",INSERT_FIELDS,
            ")values(#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);

    //根据一个实体找出它所有的评论
    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME,
            "where entity_id=#{entityId} and entity_type=#{entityType} order by created_date desc"})
    List<Comment> selectCommentByEntity(@Param("entityId")int entityId,@Param("entityType")int entityType);

    @Select({"select",SELECT_FIELDS,"from",TABLE_NAME, "where id=#{id}"})
    Comment getCommentById(int id);

    //查找某一个实体下面评论的数量
   @Select({"select count(id) from",TABLE_NAME,
           "where entity_id=#{entityId} and entity_type=#{entityType}"})
   int getCommentCount(@Param("entityId") int entityId,@Param("entityType") int entityType);

    //删除一条评论
    @Update({"update",TABLE_NAME,"set status=#{status} where id=#{id}"})
    int updateStatus(@Param("id")int id,@Param("status") int status);

    //查找用户评论的数量
    @Select({"select count(id) from",TABLE_NAME, "where user_id=#{userId}"})
    int selectUserCommentCount(@Param("userId") int userId);
}
