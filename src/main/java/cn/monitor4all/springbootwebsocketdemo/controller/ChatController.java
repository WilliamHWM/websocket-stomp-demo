package cn.monitor4all.springbootwebsocketdemo.controller;

import cn.monitor4all.springbootwebsocketdemo.model.ChatMessage;
import cn.monitor4all.springbootwebsocketdemo.service.ChatService;
import cn.monitor4all.springbootwebsocketdemo.util.JsonUtil;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

    @Value("${redis.channel.msgToAll}")
    private String msgToAll;

    @Value("${redis.set.onlineUsers}")
    private String onlineUsers;

    @Value("${redis.channel.userStatus}")
    private String userStatus;

    @Autowired
    private ChatService chatService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/test")
    public void sendMessageTest() {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSender("dd");
        chatMessage.setContent("私人聊天！");
        simpMessagingTemplate.convertAndSendToUser("isMe","/topic/chat.sendMessage", JSON.toJSONString(chatMessage));
    }
    @MessageMapping("/chat.sendMessage")
    public String sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setSender("dd");
        chatMessage.setContent("私人聊天！");
        simpMessagingTemplate.convertAndSendToUser("isMe","/chat.sendMessage", JSON.toJSONString(chatMessage));
        chatMessage.setContent("这个是测试！");
        return JSON.toJSONString(chatMessage);
    }
    @SubscribeMapping("/subChat")
    public String sendMessage() {
//        simpMessagingTemplate.convertAndSend("/chat.sendMessage", JSON.toJSONString(chatMessage));
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatMessage.setSender("test");
        chatMessage.setContent("成功订阅！");
        return JSON.toJSONString(chatMessage);
    }


    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        LOGGER.info("User added in Chatroom:" + chatMessage.getSender());
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    }

}
