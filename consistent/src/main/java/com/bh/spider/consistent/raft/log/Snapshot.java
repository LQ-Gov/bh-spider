package com.bh.spider.consistent.raft.log;

import com.bh.spider.consistent.raft.serialize.ProtoBufUtils;

/**
 * @author liuqi19
 * @version : Snapshot, 2019-04-29 17:46 liuqi19
 */
public class Snapshot {

    private Metadata metadata;

    private byte[] data;


    public Snapshot() {
    }

    public Snapshot(Metadata metadata, byte[] data) {
        this.metadata = metadata;
        this.data = data;
    }


    public Metadata metadata() {
        return metadata;
    }


    public byte[] data() {
        return data;
    }


    public static Snapshot deserialize(byte[] data) {
        return ProtoBufUtils.deserialize(data, Snapshot.class);
    }

    public byte[] serialize() {
        return ProtoBufUtils.serialize(this);
    }


    public static class Metadata {
        public final static Metadata EMPTY = new Metadata(-1, -1);

        private long term;
        private long index;


        public Metadata() {
        }

        public Metadata(long term, long index) {
            this.term = term;
            this.index = index;
        }


        public long term() {
            return term;
        }

        public long index() {
            return index;
        }


        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Metadata)) return false;

            Metadata meta = (Metadata) obj;

            return meta.term == this.term && meta.index == this.index;

        }
    }
}
