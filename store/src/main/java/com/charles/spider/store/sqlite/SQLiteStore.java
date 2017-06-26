package com.charles.spider.store.sqlite;

import com.charles.spider.store.base.Criteria;
import com.charles.spider.store.base.Store;
import com.charles.spider.store.condition.Condition;
import com.charles.spider.store.entity.Module;
import com.charles.spider.store.service.Service;

import java.sql.Connection;

/**
 * Created by lq on 17-6-22.
 */
public class SQLiteStore implements Store {
    private Connection connection;
    private SQLiteCriteriaInterpreter interpreter;
    private Service<Module> moduleService = null;
    public SQLiteStore(Connection connection) {
        this.connection = connection;
        moduleService = new SQLiteModuleService(this);
        interpreter = new SQLiteCriteriaInterpreter();

    }

    public Connection getConnection(){
        return connection;
    }


    public String explain(Condition condition) {
        return interpreter.explain(condition);
    }


    @Override
    public Service<Module> module() {
        return moduleService;
    }



}
