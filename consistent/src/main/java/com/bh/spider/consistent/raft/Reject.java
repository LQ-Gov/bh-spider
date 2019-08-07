package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version Reject, 2019-07-24 10:45 liuqi19
 **/
public class Reject {
    private boolean value;
    /**
     * 发送过来的index
     */
    private long index;

    /**
     * 当前节点的index
     */
    private long lastIndex;

    public Reject(){}

    public Reject(boolean value,long index,long lastIndex){
        this.value = value;
        this.index = index;
        this.lastIndex = lastIndex;
    }


    public boolean value(){
        return value;
    }

    public long index(){return index;}

    public long lastIndex(){
        return lastIndex;
    }
}
