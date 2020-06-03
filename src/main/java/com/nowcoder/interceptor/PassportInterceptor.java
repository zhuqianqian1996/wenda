package com.nowcoder.interceptor;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class PassportInterceptor implements HandlerInterceptor {
    @Resource
    private LoginTicketDAO loginTicketDao;

    @Resource
    private UserDAO userDAO;

    @Autowired
    private HostHolder hostHolder;

    //请求开始之前就执行preHandle方法，对请求进行拦截
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse, Object o) throws Exception {
        String ticket = null;
        LoginTicket loginTicket;
        //取出请求中cookie
        if (httpServletRequest.getCookies()!=null){
            for (Cookie cookie : httpServletRequest.getCookies()) {
                if (cookie.getName().equals("ticket")){
                    ticket = cookie.getValue();
                    break;
                }
            }
        }
        //cookie里的ticket不为空，判断这个ticket在数据库中是否过期
        if (ticket!=null){
             loginTicket = loginTicketDao.selectByTicket(ticket);
            if (loginTicket==null||loginTicket.getExpired().before(new Date())||loginTicket.getStatus()!=0){
                //token不符合要求，则拦截该请求
                return true;
            }
        //取出用户
        User user = userDAO.selectById(loginTicket.getUserId());
        //在拦截器最早的地方将user注入到Threadlocal(放在上下文)
        hostHolder.setUser(user);
      }
        return true;
    }

    //handler处理完以后，调用postHandle
    @Override
    public void postHandle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse, Object o,
                           ModelAndView modelAndView) throws Exception {
        if (modelAndView!=null && hostHolder.getUser() != null){
            //渲染之前，把user对象加入到modelAndView,可以显示user头像
           modelAndView.addObject("user",hostHolder.getUser()) ;
        }
    }

    //请求处理完毕再执行afterCompletion方法
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse, Object o,
                                 Exception e) throws Exception {
        //请求处理完毕，controller返回数据给前端进行渲染的时候，清除线程副本
        hostHolder.clear();
    }
}
