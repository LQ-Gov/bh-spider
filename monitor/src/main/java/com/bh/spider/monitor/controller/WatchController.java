package com.bh.spider.monitor.controller;

import com.bh.spider.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WatchController {

    private Client client = null;

    private SimpMessagingTemplate template;

    @Autowired
    public WatchController(Client client, SimpMessagingTemplate template)  {

        this.template = template;
        this.client = client;
        //client.watch("request.submit.count", this::send);
    }
    public void send(String data) {
        System.out.println(data);
        template.convertAndSend("/watch/point",data);
    }


}
