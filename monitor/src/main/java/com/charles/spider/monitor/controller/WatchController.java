package com.charles.spider.monitor.controller;

import com.charles.spider.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Controller
public class WatchController extends TextWebSocketHandler {

    private Client client = null;

    private SimpMessagingTemplate template;

    @Autowired
    public WatchController(Client client, SimpMessagingTemplate template) throws IOException {

        this.template = template;
        this.client = client;
        client.watch("request.submit.count", this::send);
    }
    public void send(String data) {
        System.out.println(data);
        template.convertAndSend("/watch/point",data);
    }


}
