package com.bh.spider.scheduler.cluster.consistent.operation;

import java.util.Collections;
import java.util.List;

/**
 * @author liuqi19
 * @version DiscardPersistent, 2019-08-21 00:31 liuqi19
 **/
public class DiscardPersistent implements Persistent {
    @Override
    public List<Entry> recover() throws Exception {
        return Collections.emptyList();
    }

    @Override
    public boolean write(Entry entry) throws Exception {
        return true;
    }

    @Override
    public void cut(long committedIndex, Entry snap) throws Exception {

    }
}
