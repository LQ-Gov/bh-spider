package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : Vote, 2019-04-15 11:22 liuqi19
 */
public class Vote {
    private int id;

    private long term;

    private long index;

    private boolean pre;

    public Vote() {
    }


    public Vote(int id, long term, long index, boolean pre) {
        this.id = id;
        this.term = term;
        this.index = index;
        this.pre = pre;
    }


    public int id() {
        return id;
    }


    public long term() {
        return term;
    }

    public long index() {
        return index;
    }

    public boolean pre() {
        return pre;
    }


    @Override
    public String toString() {
        return "id:" + id + ",term:" + term + ",index:" + index + ",pre:" + pre;
    }
}

