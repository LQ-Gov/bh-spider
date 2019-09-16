package com.bh.spider.consistent.raft.wal;

import com.bh.spider.consistent.raft.HardState;
import com.bh.spider.consistent.raft.log.Entry;
import com.bh.spider.consistent.raft.log.Snapshot;

import java.nio.file.Path;
import java.util.List;

/**
 * @author liuqi19
 * @version DiscardWAL, 2019/9/16 10:21 上午 liuqi19
 **/
public class DiscardWAL extends WAL {


    public static DiscardWAL open(Path dir, Snapshot.Metadata metadata){
        return new DiscardWAL();
    }

    @Override
    public void sync() {
    }

    @Override
    public Stashed readAll() {
        return null;
    }

    @Override
    public synchronized void save(HardState state, List<Entry> entries) {

    }

    @Override
    public void release(long index) {

    }

    @Override
    public void save(Snapshot.Metadata metadata) {

    }
}
