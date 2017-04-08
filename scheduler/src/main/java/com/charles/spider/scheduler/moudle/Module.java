package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.moudle.Description;

/**
 * Created by lq on 17-4-8.
 */
public class Module {
    private Description description;
    public Module(Description desc) {
        this.description = desc;
    }

    public Description getDescription() {
        return description;
    }
}
