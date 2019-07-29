package com.bh.spider.scheduler.event;

/**
 * @author liuqi19
 * @version TestAssistant, 2019-07-29 11:15 liuqi19
 **/
public class TestAssistant1 implements Assistant {

    public int i=0;


    @CommandHandler
    public void TEST1_HANDLER(){
        i++;
    }



}
