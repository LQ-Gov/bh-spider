package com.bh.spider.consistent.raft.wal;

/**
 * @author liuqi19
 * @version : RecordType, 2019-04-23 16:18 liuqi19
 */
public enum  RecordType {
    ENTRY,
    STATE,
    METADATA,
    SNAPSHOT
}
