package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.node.RaftNode;

import java.util.concurrent.CompletableFuture;

/**
 * @author liuqi19
 * @version RaftContext, 2019-07-22 14:29 liuqi19
 **/
public class RaftContext {

    private RaftNode node;

    private CompletableFuture future;


    public RaftContext(RaftNode node, CompletableFuture future){
        this.node = node;
        this.future = future;

    }


    public RaftNode from(){
        return node;
    }


    public void completeExceptionally(Throwable e){
        if(future!=null)
            future.completeExceptionally(e);
    }
}
