package com.bh.spider.ui.controller;

import com.bh.common.watch.WatchEvent;
import com.bh.spider.client.Client;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.ui.watch.TimerWatchConsumer;
import com.bh.spider.ui.watch.WatchConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WatchController {

    private Client client;

    @Autowired
    public WatchController(Client client) {

        this.client = client;
    }


    @GetMapping(value = "/watch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public synchronized SseEmitter watch(HttpServletRequest request, @RequestParam("point") String[] points) throws Exception {

        SseEmitter emitter = new SseEmitter(0L);

        Map<String, WatchConsumer> map = new HashMap<>();
        for (String point : points) {
            String[] block = point.split(":");
            String key = block[0];
            String params = block.length == 2 ? block[1] : null;

            switch (key) {
                case "url.distribute":
                    break;

                //rule 对应的url数量统计
                case "rule.url.count":
                    final Long id = params == null ? null : Long.parseLong(params);
                    map.put(point, new TimerWatchConsumer<>(client, emitter, point, () -> {
                        Map<String, Long> urls = new HashMap<>();
                        urls.put("queue", client.request().count(id, Request.State.QUEUE));
                        urls.put("error", client.request().count(id, Request.State.EXCEPTION));
                        urls.put("going", client.request().count(id, Request.State.GOING));
                        return urls;
                    }));
                    break;


                default: {
                    WatchConsumer<WatchEvent> consumer = new WatchConsumer<>(client, emitter, point);

                    if (this.client.watch(point, consumer)) {
                        map.put(point, consumer);
                    }
                }
            }


        }


        Runnable runnable = () -> {

            map.forEach((k, v) -> {
                v.close();
                client.unwatch(k, v);
            });
            System.out.println("终止");
        };


        emitter.onCompletion(runnable);

        emitter.onTimeout(runnable);

        emitter.onError(throwable -> runnable.run());

        return emitter;

    }


}
