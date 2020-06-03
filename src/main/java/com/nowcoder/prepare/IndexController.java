package com.nowcoder.prepare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

//@Controller
public class IndexController {
    @Autowired
    WendaService wendaService;
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping(path = "/index")
    @ResponseBody
     public String index(HttpSession session){
         logger.info("visit home");
         return wendaService.getMessage(1)+ "hello NowCoder"+session.getAttribute("msg");

     }

     @RequestMapping(path = {"/profile/{groupId}/{userId}"},method = RequestMethod.GET)
     @ResponseBody
    public String profile(@PathVariable("userId")int userId,
                          @PathVariable("groupId") String groupId,
                          @RequestParam("type") int type,
                          @RequestParam("key") String key){
       return String.format("Profile page of %s / %d  t:%d  k:%s",groupId,userId,type,key);
     }

    @RequestMapping(path = "/vm",method = RequestMethod.GET)
    public String template(){

        return "home";
    }

    @RequestMapping(path = "/request",method = RequestMethod.GET)
    @ResponseBody
    public String template(Model model, HttpServletResponse response,
                           HttpServletRequest request, HttpSession session,
                           @CookieValue("JSESSIONID") String sessionId){
         StringBuilder sb = new StringBuilder();
         sb.append(sessionId);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name+":"+request.getHeaders(name));
        }
        if(request.getCookies()!=null){
            for (Cookie cookie : request.getCookies()) {
                sb.append("Cookie:"+cookie.getName()+"   <br>   "+"Value:"+cookie.getValue());
            }
        }
        response.addHeader("NowCoderId","hello");
        response.addCookie(new Cookie("username","coder"));
        return sb.toString();
    }

    @RequestMapping(value = "/redirect/{code}", method = RequestMethod.GET)
    public RedirectView redirect(@PathVariable("code")int code,
                           HttpSession session){
        session.setAttribute("msg","jump form redirect");
        RedirectView redirectView = new RedirectView("/index",true);
        if (code == 301){
            //如果是301就给设置状态码为301，强制跳转
            redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return redirectView;
    }

    @RequestMapping(path = "/admin",method = RequestMethod.GET)
    @ResponseBody
    public String admin(@RequestParam("key") String key){
        if ("admin".equals(key)){
            return "hello admin";
        }
        throw new IllegalArgumentException("参数不对");
    }

    //全局异常
    @ExceptionHandler
    @ResponseBody
    public String error(Exception e){
        return "error:"+e.getMessage();
    }
}
