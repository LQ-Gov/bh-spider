package com.bh.spider.consistent.raft.wal;

/**
 * @author liuqi19
 * @version $Id: Utils, 2019-04-01 16:51 liuqi19
 */
public class Utils {


    public static String walName(long seq, long index) {
        return String.format("%016x-%016x.wal", seq, index);
    }
}
