package com.nowcoder.model;

import lombok.Data;

import java.util.Date;

@Data
public class Question {
    private int id;
    private String title;
    private String content;
    private int userId;
    private Date createdDate;
    private int commentCount;
}
