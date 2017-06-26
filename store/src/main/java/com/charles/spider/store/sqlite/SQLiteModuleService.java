package com.charles.spider.store.sqlite;

import com.charles.spider.common.moudle.ModuleType;
import com.charles.spider.store.base.Query;
import com.charles.spider.store.condition.Condition;
import com.charles.spider.store.entity.Module;
import com.charles.spider.store.service.Service;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by lq on 17-6-22.
 */
public class SQLiteModuleService implements Service<Module> {
    private final static String MODULE_TABLE_NAME = "charles_spider_module";

    private SQLiteStore store = null;
    private Connection connection;

    public SQLiteModuleService(SQLiteStore store) {
        this.store = store;
        this.connection = store.getConnection();

    }


    public synchronized void init() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + MODULE_TABLE_NAME + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "path TEXT," +
                "hash TEXT," +
                "type TEXT," +
                "detail TEXT," +
                "update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        connection.prepareStatement(sql).execute();

    }

    @Override
    public Module save(Module entity) {
        try {

            String sql = "INSERT INTO " + MODULE_TABLE_NAME + "(name,path,hash,type,detail) VALUES(?,?,?,?,?)";

            PreparedStatement stat = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stat.setObject(1, entity.getName());
            stat.setObject(2, entity.getPath());
            stat.setObject(3, entity.getHash());
            stat.setObject(4, entity.getType());
            stat.setObject(5, entity.getDetail());

            stat.execute();

            ResultSet rs = stat.getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getLong(1));
                entity.setUpdateTime(new Date());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


        return entity;
    }

    @Override
    public List<Module> select(Query query) {

        String sql = "SELECT * FROM " + MODULE_TABLE_NAME;

        //处理查询条件
        if (query != null) {
            Iterator<Condition> it = query.chain();

            String where = "";
            while (it.hasNext()) {
                where += " AND " + store.explain(it.next());
            }

            if (where.startsWith(" AND ")) where = where.replaceFirst(" AND ", " WHERE ");
            if (!StringUtils.isBlank(where)) sql += where;
        }

        try {
            ResultSet rs = connection.prepareStatement(sql).executeQuery();

            List<Module> result = new ArrayList<>();

            while (rs.next()) {
                Module module = new Module();
                module.setName(rs.getString("name"));
                module.setPath(rs.getString("path"));
                module.setHash(rs.getString("hash"));
                module.setUpdateTime(rs.getTimestamp("update_time",Calendar.getInstance(TimeZone.getTimeZone("UTC"))));
                module.setType(ModuleType.valueOf(rs.getString("type")));
                module.setDetail(rs.getString("detail"));
                module.setId(rs.getLong("id"));
                result.add(module);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void delete(Module entity) {

    }

    @Override
    public void update(Module entity) {

    }


}
