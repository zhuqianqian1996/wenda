package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.utils.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDao;

    /**
     * 注册功能
     * @param username
     * @param password
     * @return
     */
    public Map<String,String> register(String username,String password){
        //设置一个map存放返回给controller层调用由model返回给前端
        Map<String,String> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        //判断用户名是否被注册
        User user = userDAO.selectByName(username);
        if (user != null){
            map.put("msg","用户名被注册");
            return map;
        }
        //如果用户名没有被注册，则创建新用户并存储到数据库中
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        userDAO.addUser(user);
        //用户名和密码都确认无误的之后设置cookie的值和用户id关联，然后存入到map
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    /**
     * 登录功能
     * @param username
     * @param password
     * @return
     */
    public Map<String,Object> login(String username,String password){
        Map<String,Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if (user==null){
            map.put("msg","用户名不存在");
            return map;
        }
        //逆向判断MD5加密密码和数据库的加盐密码是否一致
        if (!WendaUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码错误");
            return map;
        }
        //用户名和密码都确认无误的之后设置cookie的值和用户id关联，然后存入到map
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    //增加token的方法，下发一个ticket，关联userId
    public String addLoginTicket(int userId){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(userId);
        Date now = new Date();
        now.setTime(now.getTime()+3600*24*60);
        loginTicket.setExpired(now);
        loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicket.setStatus(0);
        loginTicketDao.addTicket(loginTicket);
        return loginTicket.getTicket();
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    //改变ticket的状态
    public void logout(String ticket){
        //改变ticket的状态码，使token失效
        loginTicketDao.updateStatus(ticket,1);
    }

    public User getUserByName(String name){
        return userDAO.selectByName(name);
    }

}
