package com.nowcoder.service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentDAO commentDao;

    @Autowired
    SensitiveService sensitiveService;

    //根据一个实体找出它所有的评论
    public List<Comment> getCommentByEntity(int entityId,int entityType){
        return commentDao.selectCommentByEntity(entityId,entityType);
    }

    //增加评论
    public int addComment(Comment comment){
        //添加的时候进行敏感词过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return commentDao.addComment(comment)>0?comment.getId():0;
    }

    //查找某一个实体下面评论的数量
    public int getCommentCount(int entityId,int entityType){
        return commentDao.getCommentCount(entityId,entityType);
    }

    public boolean deleteComment(int commentId){
        return commentDao.updateStatus(commentId,1)>0;
    }

    public Comment getCommentById(int id){
        return commentDao.getCommentById(id);
    }

    public int getUserCommentCount(int userId){
        return commentDao.selectUserCommentCount(userId);
    }
}
