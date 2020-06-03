package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    CommentService commentService;

    @Autowired
    LikeService likeService;
    /**
     * 添加问题的功能
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(value = "/question/add",method = RequestMethod.POST)
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content")String content){
        //添加问题
        try {
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());
            if (hostHolder.getUser()==null){
                // question.setUserId(WendaUtil.ANONYMOUS_USERID);
               return WendaUtil.getJsonString(999);
            }else {
                //从hostHolder中取出当前用户的id
                question.setUserId(hostHolder.getUser().getId());
            }
            if (questionService.addQuestion(question)>0){
                //请求成功都返回json串：{code：0}
                return WendaUtil.getJsonString(0);
            }
        }catch (Exception e){
            logger.error("增加问题失败"+e.getMessage());
        }
        //返回json串{code:0,msg:失败}
        return WendaUtil.getJsonString(1,"失败");
    }

    /**
     * 显示评论详情
     * @param model
     * @param qid
     * @return
     */
    @RequestMapping("/question/{qid}")
    public String getQuestionDetail(Model model, @PathVariable("qid")int qid){
        Question question = questionService.getQuestionById(qid);
        //model将question传递给前端页面展示
        model.addAttribute("question",question);
        //把用户传递给前端页面展示
        model.addAttribute("user",userService.getUser(question.getUserId()));
        //找到全部的comment
        List<Comment> commentList = commentService.getCommentByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> comments = new ArrayList<>();
        for (Comment comment : commentList) {
            ViewObject vo = new ViewObject();
            vo.set("comment",comment);
            if (hostHolder.getUser()==null){
                vo.set("liked",0);
            }else {
                //关联状态
                vo.set("liked",likeService.getLikeStatus(hostHolder.getUser().getId(),
                        EntityType.ENTITY_COMMENT,comment.getEntityId()));
            }
            //点赞数
            vo.set("likeCount",likeService.getLikeCount(EntityType.ENTITY_COMMENT,comment.getId()));
            //用户头像
            vo.set("user",userService.getUser(comment.getUserId()));
            comments.add(vo);
        }
        model.addAttribute("comments",comments);
        return "detail";
    }

}
