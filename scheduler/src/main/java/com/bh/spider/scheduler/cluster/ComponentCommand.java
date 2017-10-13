package com.bh.spider.scheduler.cluster;

import com.bh.spider.transfer.entity.Component;
import io.atomix.copycat.Command;

public class ComponentCommand {

    public static class Submit implements Command<Void>{
        public Component value;

        public Submit(Component value){
            this.value = value;
        }
    }

    public static class Delete implements Command<Void>{
        public String name;

        public Delete(String name){
            this.name = name;
        }
    }
}
