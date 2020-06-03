package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @Autowired
    EventProducer eventProducer;

    /**
     * 登录注册页面
     * @return
     */
    @RequestMapping(path = {"/relogin"},method = RequestMethod.GET)
    public String reg(Model model, @RequestParam(value = "next",required = false) String next){
        //next参数埋到前端页面属性中
        model.addAttribute("next",next);
        return "login";
    }

    /**
     * 注册功能
     * @param model
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(path = {"/reg/"} ,method = RequestMethod.POST)
    public String register(Model model, @RequestParam("username")String username,
                           @RequestParam("password")String password,
                           @RequestParam("next")String next,
                           @RequestParam("rememberme")String rememberme,
                           HttpServletResponse response){
        try {
            //验证用户名和密码
            Map<String, String> map = userService.register(username, password);
            //如果map中包含token，说明用户名和密码无误，token保存到浏览器的cookie中
            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                response.addCookie(cookie);
                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                model.addAttribute("msg","注册成功，请输入您的用户名和密码");
                return "login";
            } else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("注册异常"+e.getMessage());
            return "login";
        }
    }

    /**
     * 登录功能
     * @param model
     * @param username
     * @param password
     * @param rememberme
     * @return
     */
    @RequestMapping(path = {"/login/"} ,method = RequestMethod.POST)
    public String login(Model model,
                        @RequestParam("username")String username,
                        @RequestParam("password")String password,
                        @RequestParam(value = "rememberme",defaultValue="false") boolean rememberme,
                        @RequestParam(value = "next",required = false)String next,
                        HttpServletResponse response){
        try {
            //验证用户名和密码
            Map<String,Object> map = userService.login(username,password);
            //把ticket票下发给浏览器
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                response.addCookie(cookie);

               //异步队列处理登录请求,登录失败就发送邮件
//                 eventProducer.fireEvent(new EventModel(EventType.LOGIN)
//                 .setActorId((int)map.get("userId")).setExt("username",username)
//                 .setExt("email","2305608098@qq.com"));

                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";
            }else {
                model.addAttribute("msg",map.get("msg"));
                return "login";
            }
        }catch (Exception e){
            logger.error("登录异常"+e.getMessage());
            return "login";
        }
    }


    /**
     * 退出功能
     * @param ticket
     * @return
     */
    @RequestMapping(path = {"/logout"} ,method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket) {
        //清除掉token
        userService.logout(ticket);
        return "redirect:/";
    }

}
