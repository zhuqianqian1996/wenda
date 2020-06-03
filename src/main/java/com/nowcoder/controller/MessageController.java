package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.WendaUtil;
import jdk.internal.org.objectweb.asm.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;


    /**
     * 添加私信
     * @param toName
     * @param content
     * @return
     */
    @RequestMapping(path = "/msg/addMessage",method = RequestMethod.POST)
    @ResponseBody
    public String addComment(@RequestParam("toName") String toName,
                             @RequestParam("content") String content){
        try {
             if (hostHolder.getUser()==null){
                 return WendaUtil.getJsonString(999,"未登录");
             }
             User user = userService.getUserByName(toName);
             if (user==null){
                 return WendaUtil.getJsonString(1,"用户不存在");
             }
             Message message = new Message();
             message.setFromId(hostHolder.getUser().getId());
             message.setToId(user.getId());
             message.setContent(content);
             message.setCreatedDate(new Date());
             message.setHasRead(0);
             messageService.addMessage(message);
             return WendaUtil.getJsonString(0);
        }catch (Exception e){
            logger.error("增加站内信失败"+e.getMessage());
            return WendaUtil.getJsonString(1,"增加站内信失败");
        }
    }

    /**
     *获取站内信列表
     * @param model
     * @return
     */
    @RequestMapping(path = "/msg/list",method = RequestMethod.GET)
    public String getConversationList(Model model){
        try {
            if (hostHolder.getUser()==null){
                return "redirect:/relogin";
            }
            int localUserId = hostHolder.getUser().getId();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            List<ViewObject> conversations = new ArrayList<>();
            for (Message message : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                int targetId = message.getFromId()==localUserId?message.getToId():message.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user",user);
                vo.set("unread",messageService.getConvesationUnreadCount(localUserId,message.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations",conversations);
        }catch (Exception e){
            logger.error("获取站内信列表失败"+e.getMessage());
        }
        return "letter";
    }



    /**
     * 获取私信详情List
     * @param model
     * @param conversationId
     * @return
     */
    @RequestMapping(path = "/msg/detail",method = RequestMethod.GET)
    public String getConversationDetail(Model model,
                                        @RequestParam("conversationId") String conversationId){
        try {
            List<Message> MessageList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message message : MessageList) {
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                User user = userService.getUser(message.getFromId());
                vo.set("user",user);
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
        }catch (Exception e){
            logger.error("获取详情失败"+e.getMessage());
        }
        return "letterDetail";
    }
}
