package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.log.DiscardSnapshotter;
import com.bh.spider.consistent.raft.log.Log;
import com.bh.spider.consistent.raft.wal.DiscardWAL;

/**
 * @author liuqi19
 * @version InstableRaft, 2019/8/25 10:10 下午 liuqi19
 **/
public class UnstableRaft extends Raft {

    public UnstableRaft(Actuator actuator) throws IllegalAccessException {
        super(null, actuator);
    }


    @Override
    protected synchronized void exec() throws Exception {

        this.initLog(new Log(this, new DiscardSnapshotter(null), new DiscardWAL(), actuator()));

        this.becomeFollower(0, null);
    }
}
