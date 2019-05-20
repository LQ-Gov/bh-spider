package com.bh.spider.consistent.raft.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author liuqi19
 * @version : Unstable, 2019-04-19 15:03 liuqi19
 */
public class Unstable {
    private final static Logger logger = LoggerFactory.getLogger(Unstable.class);


    private long offset;
    private List<Entry> entries = new LinkedList<>();

    public Unstable(){}

    public Unstable(long offset){
        this.offset = offset;
    }


    public synchronized void append(Entry[] entries) {

        long after = entries[0].index();

        if (offset + this.entries.size() == after)
            this.entries.addAll(Arrays.asList(entries));

            // The log is being truncated to before our current offset
            // portion, so set the offset and replace the entries
        else if (after <= offset) {
            offset = after;
            this.entries = new LinkedList<>(Arrays.asList(entries));
        } else {
            logger.info("truncate the unstable entries before index {}", after);
            // truncate to after and copy to u.entries
            // then append

            this.entries = new LinkedList<>(this.entries.subList(0, (int) (after-offset)));

            this.entries.addAll(Arrays.asList(entries));

        }
    }



    public synchronized List<Entry> entries() {

        return Collections.unmodifiableList(new LinkedList<>(entries));
    }

    public long term(long index){
        return entries.get((int) (index-offset)).term();
    }

    public long firstIndex(){
        return entries.isEmpty()?-1:entries.get(0).index();
    }


    public long offset(){
        return offset;
    }

    public long lastIndex(){
        if(!entries.isEmpty())
            return offset+entries.size()-1;

        return -1;
    }


    public synchronized List<Entry> stableTo(long term,long index) {

        if (index >= offset) {

            List<Entry> stabled = this.entries.subList(0, (int) (index - offset) + 1);

            this.entries = new LinkedList<>(this.entries.subList((int) (index - offset) + 1, entries.size()));

            this.offset = index+1;

            return stabled;
        }
        return null;
    }




}
