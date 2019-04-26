package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MessageController {
    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/controller/{id}")
    public void handleChat(@DestinationVariable String id, String msg) {
        // ルームにメッセージを送信
        messagingTemplate.convertAndSend("/topic/" + id, msg);

        // メンションがあるか確認
        if(!(msg.contains("@") && msg.contains(" "))) {
            return;
        }

        // メンションの宛先抽出
        List<String> targets = new ArrayList<String>();
        String tmpMsg = msg;
        while(tmpMsg.contains("@") && tmpMsg.contains(" ")){
            int start = tmpMsg.indexOf("@")+1;
            int end = tmpMsg.indexOf(" ");
            String target = tmpMsg.substring(start, end);
            tmpMsg = tmpMsg.replaceAll("@" + target + " ", "");
            targets.add(target);
        }

        // 宛先にメンションを飛ばす
        for(String target: targets) {
            messagingTemplate.convertAndSend("/mention/" + target, "mention!!");
        }

    }
}