package com.charles.store.sqlite;

import com.charles.store.base.Field;
import com.charles.store.stantment.Statement;
import com.charles.store.base.Store;
import com.charles.store.base.Target;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-3-24.
 */
public class SqliteStore implements Store {
    private static Map<Target,String> tables = new HashMap<>();
    static {
        tables.put(Target.TASK, "task");
        tables.put(Target.MOUDLE, "moudle");
        tables.put(Target.TIMER, "timer");
    }
    @Override
    public Statement select(Target target, Field... fields) {
        return new SqliteSelectStatement(target, fields);
    }

    @Override
    public Statement insert(Target target, Field... values) {
        return new SqliteInsertStatement(target, values);
    }

    @Override
    public Statement update(Target target, Field... values) {
        return new SqliteUpdateStatement(target, values);
    }
}
