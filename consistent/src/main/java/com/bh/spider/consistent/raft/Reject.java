package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version Reject, 2019-07-24 10:45 liuqi19
 **/
public class Reject {
    private boolean value;

    private long index;

    public Reject(){}

    public Reject(boolean value,long index){
        this.value = value;
        this.index = index;
    }


    public boolean value(){
        return value;
    }

    public long index(){
        return index;
    }
}
