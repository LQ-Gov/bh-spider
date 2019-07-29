package com.bh.spider.scheduler.event;

/**
 * @author liuqi19
 * @version TestAssistant3, 2019-07-29 13:07 liuqi19
 **/
public class TestAssistant3 implements Assistant {

    public int i=0;


    @CommandHandler
    public void TEST3_HANDLER(){
        i++;
    }

}
