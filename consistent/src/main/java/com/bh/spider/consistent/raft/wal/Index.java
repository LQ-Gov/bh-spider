package com.bh.spider.consistent.raft.wal;

/**
 * @author liuqi19
 * @version $Id: Index, 2019-04-03 19:33 liuqi19
 */
public class Index {
    private long index;
    private long term;




    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getTerm() {
        return term;
    }

    public void setTerm(long term) {
        this.term = term;
    }
}
