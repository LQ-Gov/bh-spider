package com.bh.spider.scheduler;

import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.EventLoop;
import com.bh.spider.common.member.Node;

import java.util.concurrent.CompletableFuture;

/**
 * 调度器接口,整个调度器都以命令的形式执行，依赖{@link com.bh.spider.scheduler.event.EventLoop},进行异步执行
 */
public interface Scheduler {

    /**
     * 处理命令
     * @param cmd
     * @param <R>
     * @return
     */
    default  <R> CompletableFuture<R> process(Command cmd){
        return eventLoop().execute(cmd);
    }

    /**
     * 执行调度器
     * @throws Exception
     */
    void exec() throws Exception;

    /**
     * 配置信息
     * @return
     */
    Config config();


    /**
     * 事件循环器
     * @return
     */
    EventLoop eventLoop();


    /**
     * 本机节点信息
     * @return
     */
    Node self();


    default boolean running(){
        return eventLoop() != null && eventLoop().running();
    }



}
