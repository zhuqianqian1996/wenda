package com.nowcoder.service;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveService sensitiveService;

    //添加问题
    public int addQuestion(Question question){
        //HTML标签过滤:通过转义字符实现
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        //敏感词过滤
        question.setContent(sensitiveService.filter(question.getContent()));
        question.setTitle(sensitiveService.filter(question.getTitle()));
        //添加问题成功返回question的Id，失败返回0
        return questionDAO.addQuestion(question) > 0?question.getId():0;
    }

    //首页展示近期被提出的问题
    public List<Question> getLatestQuestion(int userId, int offset, int limit){
        return questionDAO.selectLaststQuestions(userId,offset,limit);
    }


    public Question getQuestionById(int id){
       return questionDAO.getById(id);
    }

    public int updateCommentCount(int id,int commentCount){
        return questionDAO.updateCommentCount(id,commentCount);
    }
}
