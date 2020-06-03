package com.nowcoder.model;

import lombok.Data;

import java.util.Date;

@Data
public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String content;
    private Date createdDate;
    private int hasRead;
    private String conversationId;

    public String getConversationId() {
        //保证A发给B和B发给A时conversationId一致
        if (fromId<toId){
            return String.format("%d_%d",fromId,toId);
        }else {
            return String.format("%d_%d",toId,fromId);
        }
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
