package com.bh.spider.ui.controller;

import com.bh.spider.client.Client;
import com.bh.spider.ui.watch.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WatchController {

    private Client client;

    private SimpMessagingTemplate template;

    @Autowired
    public WatchController(Client client, SimpMessagingTemplate template)  {

        this.template = template;
        this.client = client;
    }
    @SubscribeMapping("/event.loop.count")
    public void elc() throws Exception {
        Watcher.watch("event.loop.count", () -> {
            client.watch("event.loop.count", Long.class, value ->{
                System.out.println(value);
                template.convertAndSend("/topic/event.loop.count", value);});
            return true;
        });
    }






}
