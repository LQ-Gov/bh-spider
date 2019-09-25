package com.bh.spider.scheduler;

import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.CommandHandler;

/**
 * @author liuqi19
 * @version BasicSchedulerCommonAssistant, 2019/9/18 2:41 下午 liuqi19
 **/
public class BasicSchedulerCommonAssistant implements Assistant {




    @CommandHandler
    public long ID_GENERATOR_HANDLER(){
        return IdGenerator.instance.nextId();
    }
}
