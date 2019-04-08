package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : MessageHandler, 2019-04-08 14:36 liuqi19
 */
public interface MessageHandler {


    void handle(Message msg) throws Exception;
}
