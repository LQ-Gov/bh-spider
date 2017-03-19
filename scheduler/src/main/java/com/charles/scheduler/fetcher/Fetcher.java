package com.charles.scheduler.fetcher;

import com.charles.scheduler.BasicScheduler;
import com.charles.scheduler.data.moudle.Moudle;
import com.charles.scheduler.event.EventLoop;
import com.charles.scheduler.event.EventType;
import com.charles.scheduler.event.IEvent;
import com.charles.scheduler.task.Task;
import io.netty.handler.codec.http.HttpMethod;
import org.apache.http.client.methods.*;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by lq on 17-3-17.
 */
public class Fetcher implements IEvent {



    private EventLoop loop = new EventLoop(this);
    private BasicScheduler scheduler =null;
    private CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create().build();


    public Fetcher(BasicScheduler scheduler) {
        this.scheduler = scheduler;
    }


    @Override
    public Future process(EventType event,Object... params) {
        if(Thread.currentThread()!=loop)
            return loop.execute(event,params);
        switch (event){
            case TASK:this.scheduler.process(event,params);
        }

        return null;

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    public void fetch(Task task) {

        FetcherContext context = new FetcherContext();
        HttpRequestBase request = pack_request_from_task(task, context);

        exec_request_prepare_moudles(task, request, context);

        client.execute(request, new FetcherCallback(this, context));


    }

    protected HttpRequestBase pack_request_from_task(Task task,FetcherContext context) {
        HttpRequestBase request = null;
        if(task.getMethod()== HttpMethod.GET)
            request = new HttpGet(task.getUrl());
        else if(task.getMethod()==HttpMethod.POST)
            request = new HttpPost(task.getUrl());
        else if(task.getMethod()==HttpMethod.DELETE)
            request = new HttpDelete(task.getUrl());
        else if(task.getMethod()==HttpMethod.PUT)
            request = new HttpPut(task.getUrl());

        init_request_header(request,task);
        return request;
    }

    protected void init_request_header(HttpRequestBase request,Task task) {
        //Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+")

        if (task.getHeaders() != null) {
            task.getHeaders().entrySet().forEach(header -> request.setHeader(header.getKey(), header.getValue()));
        }

    }

    protected void exec_request_prepare_moudles(Task task,HttpRequestBase request,FetcherContext context) {
        List<String> prepare = task.getHandlers().get("prepare");
        if (prepare != null && prepare.size() > 0) {
            prepare.forEach(x -> {
                try {
                    Moudle moudle = (Moudle) this.scheduler.process(EventType.GET_MOUDLE).get();
                    //此次需执行moudle
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }
    }



}
