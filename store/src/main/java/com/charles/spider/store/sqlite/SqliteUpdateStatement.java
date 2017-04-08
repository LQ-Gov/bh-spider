package com.charles.spider.store.sqlite;

import com.charles.spider.store.base.Field;
import com.charles.spider.store.base.Target;
import com.charles.spider.store.stantment.AbstractStatement;

/**
 * Created by lq on 17-3-27.
 */
public class SqliteUpdateStatement extends AbstractStatement {
    public SqliteUpdateStatement(Target target, Field... fields) {
        super(target, fields);
    }

    @Override
    public <T> T exec() {
        return null;
    }
}
