package com.bh.spider.consistent.raft.node;

/**
 * @author liuqi19
 * @version : RaftNode, 2019-05-24 14:30 liuqi19
 */
public class RaftNode extends Node {

    private long index=-1;

    private long next;


    private boolean paused = false;



    public RaftNode(Node node) {
        super(node);
        this.next = index+1;
    }

    public long next(){
        return next;
    }

    public long index(){
        return index;
    }


    /**
     * 更改index
     * @param index
     * @return
     */
    public boolean advance(long index) {
        if (this.index < index) {
            this.index = index;
            this.next = index+1;
        }
        return true;
    }

    public boolean isPaused() {
        return paused;
    }


    public void pause() {
        paused = true;
    }

    /**
     * 重新启动
     */
    public void resume() {
        paused = false;
    }
}
