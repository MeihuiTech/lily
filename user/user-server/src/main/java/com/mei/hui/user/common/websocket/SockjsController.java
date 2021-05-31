package com.mei.hui.user.common.websocket;

import com.mei.hui.user.entity.SysUser;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SockjsController {

    @Autowired
    private SimpMessagingTemplate messagTemplate;

    @MessageMapping("/say")
    @SendTo("/topic")
    public String say(String body){
        return body;
    }

    @RequestMapping("/send")
    public void send(@RequestParam String msg){
        SysUser user=new SysUser();
        user.setUserId(1L);
        user.setUserName("吧哦哦红建");
        messagTemplate.convertAndSendToUser(user.getUserId()+"","/queue/getResponse",user);
    }
}
