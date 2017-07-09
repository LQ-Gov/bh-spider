package com.charles.spider.scheduler.fetcher;

import com.charles.common.HttpMethod;
import com.charles.spider.scheduler.BasicScheduler;
import com.charles.spider.scheduler.moudle.ModuleCoreFactory;
import com.charles.spider.scheduler.event.EventLoop;
import com.charles.common.spider.command.Commands;
import com.charles.spider.scheduler.event.IEvent;
import com.charles.spider.scheduler.processor.ProcessGroup;
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
    private BasicScheduler scheduler = null;
    private CloseableHttpAsyncClient client = HttpAsyncClientBuilder.create().build();
    private ProcessGroup processGroup = new ProcessGroup();


    public Fetcher(BasicScheduler scheduler) {
        this.scheduler = scheduler;
    }


    public Future process(Commands event, Object... params) {
        if (Thread.currentThread() != loop)
            return loop.execute(event, params);
        switch (event) {
//            case TASK:
//                return this.scheduler.process(event, params);
//            case PROCESS:


        }
        return null;

    }

    @Override
    public boolean isClosed() {
        return false;
    }

//    public void fetch(Task task) {
//
//
//        HttpRequestBase request = pack_request_from_task(task);
//
//        FetcherContext context = new FetcherContext(request, task);
//        exec_request_prepare_moudles(task, request, context);
//
//        client.execute(request, new FetcherCallback(this, context));
//
//    }

//    protected HttpRequestBase pack_request_from_task(Task task) {
//        HttpRequestBase request = null;
//        if (task.getMethod() == HttpMethod.GET)
//            request = new HttpGet(task.getUrl());
//        else if (task.getMethod() == HttpMethod.POST)
//            request = new HttpPost(task.getUrl());
//        else if (task.getMethod() == HttpMethod.DELETE)
//            request = new HttpDelete(task.getUrl());
//        else if (task.getMethod() == HttpMethod.PUT)
//            request = new HttpPut(task.getUrl());
//
//        init_request_header(request, task);
//        return request;
//    }
//
//    protected void init_request_header(HttpRequestBase request, Task task) {
//        //Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+")
//
//        if (task.getHeaders() != null) {
//            task.getHeaders().entrySet().forEach(header -> request.setHeader(header.getKey(), header.getValue()));
//        }
//
//    }

//    protected void exec_request_prepare_moudles(Task task, HttpRequestBase request, FetcherContext context) {
//        List<String> prepare = task.getHandlers().get("prepare");
//        if (prepare != null && prepare.size() > 0) {
////            prepare.forEach(x -> {
////                try {
////                    ModuleCoreFactory moudle = (ModuleCoreFactory) this.scheduler.process(Commands.GET_MODULE).get();
////                    //此次需执行moudle
////                } catch (InterruptedException | ExecutionException e) {
////                    e.printStackTrace();
////                }
////            });
//        }
//    }
//
//
//    protected void TASK_PROCESS_HANDLE(FetcherContext context) {
//        Task task = context.getTask();
//        processGroup.execute(() -> new Processor(task, context).exec(), () -> this.scheduler.report(task.getId(), 1));
//    }


    public void close(){}
}
