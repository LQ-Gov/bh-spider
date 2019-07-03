package com.bh.spider.ui.watch;

import com.bh.spider.client.Client;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * @author liuqi19
 * @version TimerWatchConsumer, 2019-07-02 18:53 liuqi19
 **/
public class TimerWatchConsumer<T> extends WatchConsumer<T> {
    private Disposable disposable;

    public TimerWatchConsumer(Client client, SseEmitter emitter, String point, Supplier<T> supplier) {
        super(client, emitter, point);

        this.disposable = Flux.interval(Duration.ofSeconds(5))
                .map(x -> supplier.get())
                .subscribeOn(Schedulers.elastic())
                .subscribe(this);


    }


    @Override
    public void close() {
        this.disposable.dispose();
    }
}
