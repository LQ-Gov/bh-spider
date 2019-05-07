package com.bh.spider.consistent.raft.log;

/**
 * @author liuqi19
 * @version : Snapshot, 2019-04-29 17:46 liuqi19
 */
public class Snapshot {

    private Metadata metadata;

    private byte[] data;


    public Snapshot(){}

    public Snapshot(Metadata metadata, byte[] data) {
        this.metadata = metadata;
        this.data = data;
    }


    public Metadata metadata(){return metadata;}


    public byte[] data(){return data;}




    public static class Metadata{
        private long term;
        private long index;


        public Metadata(){}

        public Metadata(long term,long index){
            this.term = term;
            this.index = index;
        }


        public long term(){return term;}

        public long index(){return index;}

    }
}
