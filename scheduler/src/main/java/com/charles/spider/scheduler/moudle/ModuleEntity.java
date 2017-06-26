package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.moudle.Description;

import java.io.IOException;

/**
 * Created by lq on 17-6-15.
 */
public class ModuleEntity {
    private String name;

    private byte[] data;

    private Description desc;

    private ModuleAgent agent;



    public ModuleEntity(ModuleAgent agent, byte[] data, Description desc) {
        this.agent = agent;

        this.data = data;
        this.desc = desc;
    }



    public boolean exists(){
        return true;
    }



    public void save(boolean override) throws IOException {

        agent.save(name,data,desc,override);

    }
}
