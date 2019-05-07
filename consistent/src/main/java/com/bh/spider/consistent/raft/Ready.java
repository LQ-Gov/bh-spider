package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.log.Entry;

import java.util.List;

/**
 * @author liuqi19
 * @version : Ready, 2019-04-25 16:53 liuqi19
 */
public class Ready {

    private List<Entry> entries;

    private List<Entry> committedEntries;


    public Ready(List<Entry> entries,List<Entry> committedEntries,Object o){
        this.entries = entries;
        this.committedEntries = committedEntries;

    }


    public List<Entry> entries(){
        return entries;
    }



    public List<Entry> committedEntries(){
        return committedEntries;
    }


//    public Snapshot snapshot(){
//        return null;
//    }
}
