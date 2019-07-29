package com.bh.spider.scheduler.event;

/**
 * @author liuqi19
 * @version TestAssistant2, 2019-07-29 13:06 liuqi19
 **/
public class TestAssistant2 implements Assistant {

    public int i=0;


    @CommandHandler
    public void TEST2_HANDLER(){
        i++;
    }
}
