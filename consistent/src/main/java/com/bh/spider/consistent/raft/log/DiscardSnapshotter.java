package com.bh.spider.consistent.raft.log;

import java.io.IOException;
import java.nio.file.Path;

/**
 * @author liuqi19
 * @version DiscardSnapshotter, 2019/9/16 10:51 上午 liuqi19
 **/
public class DiscardSnapshotter extends Snapshotter {
    public DiscardSnapshotter(Path dir) {
        super(dir);
    }


    @Override
    public Snapshot load() throws IOException {
        return null;
    }

    @Override
    public long lastIndex() {
        return 0;
    }

    @Override
    public Snapshot.Metadata lastMetadata() {
        return null;
    }

    @Override
    public Snapshot lastSnapshot() {
        return null;
    }

    @Override
    public void save(Snapshot snapshot) throws IOException {
    }
}
