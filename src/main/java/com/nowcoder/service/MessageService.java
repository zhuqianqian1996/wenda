package com.nowcoder.service;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    @Autowired
    SensitiveService sensitiveService;

    //增加私信
    public int addMessage(Message message){
        //敏感词过滤
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveService.filter(message.getContent()));
        return messageDAO.addMessage(message)>0?message.getId():0;
    }

    //查询私信的内容
    public List<Message> getConversationDetail(String conversationId,int offset,int limit){
        return messageDAO.getConversationDetail(conversationId,offset,limit);
    }

    //获取回话列表
     public List<Message> getConversationList( int userId, int offset, int limit){
        return messageDAO.getConversationList(userId,offset,limit);
    }

    //获取未读的数量
    public int getConvesationUnreadCount( int userId,String conversationId){
        return messageDAO.getConvesationUnreadCount(userId,conversationId);
    }
}
