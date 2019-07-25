package com.bh.spider.consistent.raft.node;

/**
 * @author liuqi19
 * @version : RaftNode, 2019-05-24 14:30 liuqi19
 */
public class RaftNode extends Node {

    private long match = -1;

    private long next;


    private boolean paused = false;

    private boolean active = false;


    public RaftNode(Node node) {
        super(node);
        this.next = match + 1;
    }

    public long next() {
        return next;
    }

    public long match() {
        return match;
    }


    /**
     * 更改index
     *
     * @param index
     * @return
     */
    public boolean update(long index) {
        boolean updated = false;
        if (this.match < index) {
            this.match = index;
            updated = true;
        }

        if (this.next < index + 1)
            this.next = index + 1;
        return updated;
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


    public void active(boolean value) {
        this.active = value;
    }

    public boolean isActive() {
        return active;
    }

    /**
     * 变为复制者
     */
    public void becomeProbe(){

        this.next = this.match+1;

    }
}
