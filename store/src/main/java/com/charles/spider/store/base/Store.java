package com.charles.spider.store.base;

import com.charles.spider.store.sqlite.SqliteStore;
import com.charles.spider.store.stantment.Statement;

/**
 * Created by lq on 17-3-25.
 */
public interface Store {
    public static Store get(){ return new SqliteStore(); }

    Statement select(Target target, Field... fields);
    Statement insert(Target target, Field... values);
    Statement update(Target target, Field... values);




}
