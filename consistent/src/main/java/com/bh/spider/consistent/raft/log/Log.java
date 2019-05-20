package com.bh.spider.consistent.raft.log;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author liuqi19
 * @version : Log, 2019-04-08 16:05 liuqi19
 */
public class Log {

    private final static Logger logger = LoggerFactory.getLogger(Log.class);


    private Unstable unstable;


    // committed is the highest log position that is known to be in
    // stable storage on a quorum of nodes.
    private long committed;

    // applied is the highest log position that the application has
    // been instructed to apply to its state machine.
    // Invariant: applied <= committed
    private long applied;


    private List<Entry> entries = new LinkedList<>();


    public Log() {
        this(null,null);
    }


    public Log(Snapshot snapshot,List<Entry> entries) {
        long offset = 0, committed = -1;

        if (CollectionUtils.isNotEmpty(entries)) {
            offset = entries.get(entries.size() - 1).index();
            committed = entries.get(0).index() - 1;
            this.entries = new LinkedList<>(entries);
        }

        this.unstable = new Unstable(offset);

        this.committed = committed;
    }


    public long committedIndex() {
        return committed;
    }


    public long lastIndex() {
        long i = unstable.lastIndex();
        if(i>=0) return i;

        if(!this.entries.isEmpty()){
            return this.entries.get(this.entries.size()-1).index();
        }

        return -1;
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


    public Entry entry(long index) {
        Entry[] ents = entries(index, 1);
        return ents != null && ents.length > 0 ? ents[0] : null;
    }


    public List<Entry> unstableEntries() {
        return new UnmodifiableList<>(unstable.entries());
    }


    /**
     * nextEntries returns all the available entries for execution.
     * If applied is smaller than the index of snapshot, it returns all committed
     * entries after the index of snapshot.
     */

    public List<Entry> committedEntries() {
        long off = Math.max(this.applied,this.unstable.firstIndex());

        if(committed+1>off){
            return this.slice(off,committed+1,Integer.MAX_VALUE);
        }
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


    public void stableTo(long term,long index){
        List<Entry> stabled = this.unstable.stableTo(term,index);
        if(stabled!=null){
            this.entries.addAll(stabled);
        }
    }


    public long offset(){
        return unstable.offset();
    }


    public long appliedIndex(){
        return applied;
    }


    public void applyTo(long index) {

        if (index > committed || applied > index) {
            logger.error("applied({}) is out of range [prevApplied({}), committed({})]", index, applied, committed);
            return;
        }
        this.applied = index;
    }






//    public long term(int )
}
