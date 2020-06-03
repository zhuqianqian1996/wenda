package com.nowcoder.model;

import lombok.Data;

import java.util.Date;

@Data
public class LoginTicket {

   private int id;
   private int userId;
   private String ticket;
   private Date expired;
   private int status;
}
