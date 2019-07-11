package com.bh.spider.ui.watch;

import com.bh.spider.client.Client;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.function.Consumer;

/**
 * @author liuqi19
 * @version WatchConsumer, 2019-06-21 17:41 liuqi19
 **/
public class WatchConsumer<T> implements Consumer<T> {
    private Client client;
    private SseEmitter emitter;
    private String point;

    public WatchConsumer(Client client, SseEmitter emitter, String point) {
        this.client = client;
        this.emitter = emitter;
        this.point = point;


    }


    protected Client client(){return client;}


    protected String point(){return point;}

    protected SseEmitter emitter(){return emitter;}


    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public void accept(T t) {
        try {
            emitter.send(SseEmitter.event().name(point).data(t));
        } catch (Exception e) {
            emitter.complete();
        }
    }


    public void close(){}
}
