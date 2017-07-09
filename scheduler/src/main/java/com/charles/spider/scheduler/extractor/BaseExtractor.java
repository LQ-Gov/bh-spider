package com.charles.spider.scheduler.extractor;

import com.charles.spider.scheduler.context.Context;

/**
 * Created by lq on 7/6/17.
 */
public class BaseExtractor implements Extractor {


    @Override
    public Extractor next() {
        return null;
    }

    @Override
    public boolean support(Context ctx) {
        return false;
    }

    @Override
    public void process(Context ctx) {

    }
}
