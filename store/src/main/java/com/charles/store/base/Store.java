package com.charles.store.base;

/**
 * Created by lq on 17-3-25.
 */
public interface Store {
    public static Store get(){ return null; }

    Statement select(Field... fields);
    void insert(Field... values);
    void update(Field... values);


}
