package com.bh.spider.store.base;

import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;

import java.util.List;

public class Service<T> {

    private Store store;
    private Class<T> cls;

    public Service(Store store, Class<T> cls) {
        this.store = store;
        this.cls = cls;
    }


    public T insert(T o) {
        Entity entity = store.insert(o);

        return entity == null ? null : (T) entity.toObject();
    }

    long count(Query query) {
        return store.count(this.cls, query);
    }

    public <T> List<T> select(Query query) {

        return (List<T>) store.select(this.cls, query);
    }


    public <T> T single(Query query) {
        return (T) store.single(this.cls, query);

    }

    public int delete(Query query) {
        return this.store.delete(this.cls, query);
    }


    public int update(T o, Condition condition) {
        return this.store.update(o, condition);
    }


}
