package com.charles.spider.query;


import com.charles.spider.query.condition.Condition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Created by lq on 17-6-18.
 */
public class Query {
    private long skip;
    private long limit;

    private LinkedHashSet<Condition> chain = new LinkedHashSet<>();

    public Query() {}


    public Query addCondition(Condition... conditions) {
        chain.addAll(Arrays.asList(conditions));
        return this;
    }


    public static Query Condition(Condition... conditions){
        Query query = new Query();
        query.addCondition(conditions);
        return query;
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
