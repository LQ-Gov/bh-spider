package com.bh.spider.consistent.raft.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuqi19
 * @version : Log, 2019-04-08 16:05 liuqi19
 */
public class Log {

    private final static Logger logger = LoggerFactory.getLogger(Log.class);


    private Unstable unstable;


    private long committed = -1;


    public Log() {
        unstable = new Unstable();
    }


    public long committedIndex() {
        return committed;
    }


    public long lastIndex() {
        return unstable.lastIndex();
    }


    public boolean append(Entry[] entries) {
        unstable.append(entries);
        return true;
    }


    public void commitTo(long index) {
        // never decrease commit
        if (this.committed < index) {
            if (this.lastIndex() < index) {
                logger.error("index {} is out of range [lastIndex{}]. Was the raft log corrupted, truncated, or lost?", index, this.lastIndex());
            } else
                this.committed = index;
        }
    }


    public boolean commit(long term, long index) {

        if (index > this.committed) {
            this.commitTo(index);
            return true;
        }

        return false;

    }


    public Entry[] entries(long startIndex, int size) {
        return this.slice(startIndex, this.lastIndex() + 1, size).toArray(new Entry[0]);
    }


    public List<Entry> unstableEntries() {
        return unstable.entries();
    }


    /**
     * nextEntries returns all the available entries for execution.
     * If applied is smaller than the index of snapshot, it returns all committed
     * entries after the index of snapshot.
     */

    public List<Entry> nextEntries() {
        return null;
    }

    /**
     * 返回index所在的term
     *
     * @param index
     * @return
     */
    public long term(long index) {
        if (index < 0 || index > unstable.lastIndex())
            return -1;


        return unstable.term(index);
    }


    private List<Entry> slice(long lo, long hi, int size) {

        List<Entry> entries = new ArrayList<>();


        if (lo < unstable.offset()) {

        }


        return unstable.entries().subList((int) (lo - unstable.offset()), (int) Math.min(hi - unstable.offset(), size));
    }


//    public long term(int )
}
