package com.bh.spider.ui.websocket;

import com.bh.spider.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {
    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);

    private Map<String,Long> watchs = new ConcurrentHashMap<>();

    @Autowired
    private Client client;

    @EventListener
    public void connectHandler(SessionConnectedEvent event) {
        log.info("[ws-connected] socket connect: {}", event.getMessage());
        // do someting ...
    }

    @EventListener
    public void disconnectHandler(SessionDisconnectEvent event) {
        log.info("[ws-disconnect] socket disconnect: {}", event.getMessage());
        // do someting ...
    }

    @EventListener
    public void subscribeHandler(SessionSubscribeEvent event) {
        Message<byte[]> message = event.getMessage();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (StompCommand.SUBSCRIBE.equals(command)) {
            String sessionId = accessor.getSessionId();
            String stompSubscriptionId = accessor.getSubscriptionId();
            String destination = accessor.getDestination();
        }
    }


    @EventListener
    public void unsubscribeHandler(SessionUnsubscribeEvent event){
        int a =0;

    }
}
