package com.charles.spider.store.base;

import com.charles.spider.store.condition.Condition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Created by lq on 17-6-18.
 */
public class Query {
    private long skip;
    private long limit;

    private LinkedHashSet<Condition> chain;

    public Query() {}


    public Query addCondition(Condition... condition) {
        chain.addAll(Arrays.asList(condition));
        return this;
    }


    public Query skip(long value){
        this.skip = value;
        return this;
    }

    public long skip(){return this.skip;}

    public Query limit(long value) {
        this.limit = value;
        return this;
    }

    public long limit(){return this.limit;}


    public Iterator<Condition> chain() {
        return chain.iterator();
    }


    public Query sort(){
        return this;
    }





}
