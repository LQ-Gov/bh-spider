package com.bh.spider.consistent.raft;

import org.apache.commons.lang3.RandomUtils;

/**
 * @author liuqi19
 * @version : Election, 2019-04-17 00:08 liuqi19
 */
public class Election {

    private long elapsed;

    private long randomizedElectionTimeout;


    private final long timeout;


    public Election(long timeout){
        this.timeout = timeout;
        this.resetRandomizedElectionTimeout();
    }


    private void resetRandomizedElectionTimeout() {
        this.randomizedElectionTimeout = this.timeout + RandomUtils.nextLong(0, this.timeout);
    }



    public void reset(){
        elapsed=0;
    }


    public boolean incrementOrCompleted() {
        elapsed++;

        return elapsed >= randomizedElectionTimeout;
    }
}
