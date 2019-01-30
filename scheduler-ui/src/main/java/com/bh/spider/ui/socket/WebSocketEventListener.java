package com.bh.spider.ui.socket;

import com.bh.spider.ui.watch.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {
    private static final Logger log = LoggerFactory.getLogger(WebSocketEventListener.class);

    private final static Map<String, Set<String>> SESSION_WATCHED = new ConcurrentHashMap<>();

    @EventListener
    public void connectEventHandler(SessionConnectedEvent event) {
        log.info("[ws-connected] socket connect: {}", event.getMessage());
        // do someting ...
    }

    @EventListener
    public void disconnectEventHandler(SessionDisconnectEvent event) {
        log.info("[ws-disconnect] socket disconnect: {}", event.getMessage());

        String sessionId = event.getSessionId();

        Set<String> watched = SESSION_WATCHED.get(sessionId);
        if (watched != null) {
            synchronized (watched) {
                watched.forEach(Watcher::unwatch);
                SESSION_WATCHED.remove(sessionId);
            }
        }
    }

    @EventListener
    public void subscribeEventHandler(SessionSubscribeEvent event) {
        Message<byte[]> message = event.getMessage();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination();
        final Set<String> watched = SESSION_WATCHED.computeIfAbsent(sessionId, x -> new HashSet<>());

        if (destination != null && destination.startsWith("/topic/")) {
            String key = destination.substring("/topic/".length());

            synchronized (watched) {
                if (watched.add(key))
                    Watcher.watch(key);
            }
        }
    }


    @EventListener
    public void unsubscribeEventHandler(SessionUnsubscribeEvent event) {
        Message<byte[]> message = event.getMessage();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination();

        final Set<String> watched = SESSION_WATCHED.get(sessionId);

        if (destination != null && watched != null && destination.startsWith("/topic/")) {
            String key = destination.substring("/topic/".length());

            synchronized (watched) {
                if (watched.remove(key))
                    Watcher.unwatch(key);
            }
        }
    }
}
