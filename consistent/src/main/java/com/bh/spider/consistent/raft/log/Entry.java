package com.bh.spider.consistent.raft.log;

import com.bh.common.utils.ArrayUtils;

/**
 * @author liuqi19
 * @version : Entry, 2019-04-19 14:58 liuqi19
 */
public class Entry {
    private long term;
    private long index;


    private byte[] data;



    public Entry(){}


    public Entry(byte[] data){
        this.data = data;
    }

    public long term(){return term;}

    public long index(){return index;}


    public byte[] data(){return data;}









    public static class Collection{

        private Entry[] entries = null;


        private long term;

        private long committed;

        public Collection(){}


        public Collection(Entry[] entries){
            this(0,0,entries);
        }

        public Collection(long term,long committed, Entry[] entries){
            this.term = term;
            this.committed = committed;
            this.entries = entries;
        }


        public Entry[] entries(){ return entries;}


        public long committedIndex(){return committed;}



        public long firstIndex(){
            return ArrayUtils.isEmpty(entries)?-1:entries[0].index;
        }


        public long lastIndex(){
            return ArrayUtils.isEmpty(entries)?-1:entries[entries.length-1].index;
        }



        public void update(long term,long startIndex){
            if(entries!=null){
                for(int i=0;i<entries.length;i++){
                    entries[i].term = term;
                    entries[i].index = startIndex+i;
                }
            }

        }
    }
}
