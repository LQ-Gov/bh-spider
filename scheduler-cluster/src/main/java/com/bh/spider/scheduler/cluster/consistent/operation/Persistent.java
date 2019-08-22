package com.bh.spider.scheduler.cluster.consistent.operation;

import java.util.List;

/**
 * @author liuqi19
 * @version Persistent, 2019-08-20 23:27 liuqi19
 **/
public interface Persistent {

    List<Entry> recover() throws Exception;

    boolean write(Entry entry) throws Exception;

    void cut(long committedIndex,Entry snap) throws Exception;
}
