package com.nowcoder.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {
     //为每一条线程找到当前线程关联的变量类似于Map<ThreadID,User>，只是一个工具
     private static ThreadLocal<User> users = new ThreadLocal<>();

     public User getUser(){
         return users.get();
     }

     public void setUser(User user){
         users.set(user);
     }

     public void clear(){
         users.remove();
     }
}
