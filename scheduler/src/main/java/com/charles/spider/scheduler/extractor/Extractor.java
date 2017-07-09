package com.charles.spider.scheduler.extractor;

import com.charles.spider.scheduler.context.Context;

/**
 * Created by lq on 17-6-7.
 */
public interface Extractor {

    Extractor next();

    boolean support(Context ctx);


    void process(Context ctx);
}
