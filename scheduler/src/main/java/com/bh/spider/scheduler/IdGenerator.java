package com.bh.spider.scheduler;

import java.util.Random;

public class IdGenerator {
    public final static IdGenerator instance =IdGenerator.randomIdGenerator();
    /**
     * 时间为:2017.1.1 重要，勿改,此处为生成ID减去此时间点为最终ID,如修改会导致id重复
     */
    private final static long BEGIN_TIME = 1483200000000L;
    private long lastTime;
    private long queue;
    private long processId;

    public IdGenerator(long processId){
        this.processId = processId;
        if(processId>1024) throw new Error("processId 最大为1024");
    }

    public static IdGenerator randomIdGenerator(){
        Random random = new Random();

        return new IdGenerator(random.nextInt(1024));
    }

    public synchronized long nextId() {
        long time = System.currentTimeMillis();
        while (time < lastTime)
            time = System.currentTimeMillis();
        if (time == lastTime)
            queue++;
        else
            queue = 0;

        lastTime = time;

        //时间长度41位，processId 10位,queue 12位
        return (time - BEGIN_TIME) << (10 + 12) | (processId << 12) | queue;
    }
}
