package com.bh.spider.scheduler.event;

import java.util.concurrent.CompletableFuture;

/**
 * @author liuqi19
 * @version Chunk, 2019-07-29 13:50 liuqi19
 **/
public class Chunk {
    private CommandRunner runner;
    private Command cmd;
    private CompletableFuture future;


    public Chunk(CommandRunner runner, Command cmd, CompletableFuture future){
        this.runner = runner;
        this.cmd = cmd;
        this.future = future;

    }

    public CommandRunner runner(){return runner;}

    public Command command(){return cmd;}

    public CompletableFuture future(){return future;}
}
