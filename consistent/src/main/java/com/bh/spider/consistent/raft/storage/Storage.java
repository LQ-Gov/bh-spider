package com.bh.spider.consistent.raft.storage;

import com.bh.spider.consistent.raft.pb.Entry;
import com.bh.spider.consistent.raft.pb.Snapshot;

import java.util.List;

/**
 * @author liuqi19
 * @version $Id: Storage, 2019-04-04 00:20 liuqi19
 */
public interface Storage {

    void applySnapshot(Snapshot snapshot);


    void append(List<Entry> entries);
}
