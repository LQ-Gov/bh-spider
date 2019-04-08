package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : Log, 2019-04-08 16:05 liuqi19
 */
public class Log {


    public long committedIndex(){
        return 0;
    }


    public boolean append(){
        return true;
    }


    public void commitTo(){

    }
}
