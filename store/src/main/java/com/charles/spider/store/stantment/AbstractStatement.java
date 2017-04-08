package com.charles.spider.store.stantment;

import com.charles.spider.store.base.Field;
import com.charles.spider.store.base.Target;
import com.charles.spider.store.filter.Filter;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lq on 17-3-27.
 */
public abstract class AbstractStatement implements Statement {
    private Target target = null;
    private Field[] fields = null;
    private List<Filter> filters = null;

    public AbstractStatement(Target target, Field... fields) {
        this.target = target;
        this.fields = fields;
    }


    @Override
    public SubStatement where(Filter filter) {
        if (filters == null) filters = new LinkedList<>();
        filters.add(filter);
        return new SubStatement(this);
    }

    @Override
    public abstract <T> T exec();
}
