package com.bh.spider.ui.controller;

import com.bh.common.watch.WatchEvent;
import com.bh.spider.client.Client;
import com.bh.spider.ui.watch.WatchConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class WatchController {

    private Client client;

    @Autowired
    public WatchController(Client client) {

        this.client = client;
    }

    @GetMapping(value = "/watch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public synchronized SseEmitter watch(HttpServletRequest request, @RequestParam("point") String point) throws Exception {
        final HttpSession session = request.getSession();

//        SseEmitter emitter = new SseEmitter(300 * 1000L);

        SseEmitter emitter = new SseEmitter(10000L);

        System.out.println("建立了sse连接");

        String key = String.format("watch:%s", point);


        WatchConsumer<WatchEvent> consumer = new WatchConsumer<>(client, emitter, point);

        if (this.client.watch(point, consumer)) {
            emitter.onCompletion(() -> this.client.unwatch(point,consumer));

            emitter.onTimeout(() -> this.client.unwatch(point,consumer));

            emitter.onError(throwable -> {
                throwable.printStackTrace();
                this.client.unwatch(point,consumer);
            });

            session.setAttribute(key, consumer);

        }

        return emitter;

    }

//    @GetMapping("/watch")
//    public String


}
