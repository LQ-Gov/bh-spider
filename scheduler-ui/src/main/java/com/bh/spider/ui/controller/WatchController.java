package com.bh.spider.ui.controller;

import com.bh.common.WatchPointCodes;
import com.bh.spider.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@RestController
public class WatchController {

    private final static Set<String> ALREADY_WATCH_POINTS =ConcurrentHashMap.newKeySet();

    private Client client;

    private SimpMessagingTemplate template;
    @Autowired
    public WatchController(Client client, SimpMessagingTemplate template)  {

        this.template = template;
        this.client = client;
    }
    @SubscribeMapping("/event.loop.count")
    public void elc() {
        if (ALREADY_WATCH_POINTS.add(WatchPointCodes.EVENT_LOOP_COUNT)) {
            this.client.watch("event.loop.count",Long.class, value -> template.convertAndSend("/topic/event.loop.count", value));
        }
    }




}
