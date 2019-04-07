package com.bh.spider.consistent.raft.storage;

import com.bh.spider.consistent.raft.pb.Entry;
import com.bh.spider.consistent.raft.pb.Snapshot;

import java.util.List;

/**
 * @author liuqi19
 * @version $Id: MemoryStorage, 2019-04-02 16:46 liuqi19
 */
public class MemoryStorage implements Storage {
    private Snapshot snapshot;

    @Override
    public void applySnapshot(Snapshot snapshot) {

    }

    @Override
    public void append(List<Entry> entries) {

    }
}
