package com.bh.spider.scheduler.cluster;

import com.bh.spider.transfer.entity.Component;
import io.atomix.copycat.server.Commit;
import io.atomix.resource.ResourceStateMachine;

import java.util.Properties;

public class ComponentStateMachine extends ResourceStateMachine {
    protected ComponentStateMachine(Properties config) {
        super(config);
    }



    public void submit(Commit<ComponentCommand.Submit> commit){
        try {
            Component component = commit.operation().value;
            System.out.println(component.getName());
        }finally {
            commit.release();
        }
    }

    public void delete(Commit<ComponentCommand.Delete> commit){

    }



}
