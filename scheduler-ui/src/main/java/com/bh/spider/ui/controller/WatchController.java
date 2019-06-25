package com.bh.spider.ui.controller;

import com.bh.spider.client.Client;
import com.bh.spider.ui.watch.WatchConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class WatchController {

    private Client client;

    @Autowired
    public WatchController(Client client)  {

        this.client = client;
    }
    @SubscribeMapping("/event.loop.count")
    public void elc() throws Exception {
//        Watcher.watch("event.loop.count", () -> {
//            client.watch("event.loop.count", Long.class, value ->{
//                System.out.println(value);
//                template.convertAndSend("/topic/event.loop.count", value);});
//            return true;
//        });

    }



    @GetMapping("/watch/{point}")
    public synchronized SseEmitter watch(HttpServletRequest request, @PathVariable("point") String point) throws Exception {
        final HttpSession session = request.getSession();

        SseEmitter emitter = (SseEmitter) session.getAttribute("sse-emitter");
        if(emitter==null) {
            emitter = new SseEmitter();
            session.setAttribute("sse-emitter", emitter);
        }

        String key = String.format("watch:%s",point);

        if(session.getAttribute(key)==null) {

            WatchConsumer<String> consumer = new WatchConsumer<>(session.getId(), point);

            if (this.client.watch(point, consumer)) {
                session.setAttribute(String.format("watch:%s", point), consumer);

            }
        }


        return emitter;

    }






}
