package com.nowcoder.controller;

import com.nowcoder.model.Comment;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    /**
     * 添加评论
     * @param questionId
     * @param content
     * @return
     */
    @RequestMapping(path = "/addComment",method = RequestMethod.POST)
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        //添加评论
        try {
             Comment comment = new Comment();
             comment.setContent(content);
             //判断是否是当前用户
             if (hostHolder.getUser()==null){
                 comment.setUserId(WendaUtil.ANONYMOUS_USERID);
             }else {
                 comment.setUserId(hostHolder.getUser().getId());
             }
             comment.setEntityId(questionId);
             comment.setEntityType(EntityType.ENTITY_QUESTION);
             comment.setCreatedDate(new Date());
             comment.setStatus(0);
             commentService.addComment(comment);
            //显示评论数
            int commentCount = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),commentCount);
        }catch (Exception e){
             logger.error("添加评论失败"+e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }
}
