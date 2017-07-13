package com.charles.spider.store.sqlite;

import com.charles.spider.query.condition.Condition;
import com.charles.spider.store.base.Store;
import com.charles.spider.common.entity.Module;
import com.charles.spider.store.service.Service;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by lq on 17-6-22.
 */
public class SQLiteStore implements Store {


    private Connection connection;
    private SQLiteConditionInterpreter interpreter;
    private SQLiteModuleService moduleService = null;
    public SQLiteStore(Connection connection,Connection moduleConnection) {
        this.connection = connection;
        moduleService = new SQLiteModuleService(this,moduleConnection);

        interpreter = new SQLiteConditionInterpreter();

    }

    public Connection getConnection(){
        return connection;
    }


    public String explain(Condition condition) {
        return interpreter.explain(condition);
    }


    @Override
    public void init() throws SQLException {
        moduleService.init();
    }

    @Override
    public Service<Module> module() {
        return moduleService;
    }



}
