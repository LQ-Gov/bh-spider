package com.charles.spider.store.sqlite;

import com.charles.spider.common.constant.ModuleType;
import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.common.entity.Module;
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

    public SQLiteModuleService(SQLiteStore store,Connection connection) {
        this.store = store;
        this.connection = connection;

    }


    public synchronized void init() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + MODULE_TABLE_NAME + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "path TEXT," +
                "hash TEXT," +
                "type TEXT," +
                "detail TEXT," +
                "valid INTEGER,"+
                "update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP)";
        connection.prepareStatement(sql).execute();

    }

    @Override
    public Module insert(Module entity) {
        try {

            String sql = "INSERT INTO " + MODULE_TABLE_NAME + "(name,path,hash,type,detail,valid,update_time) VALUES(?,?,?,?,?,?,?)";

            PreparedStatement stat = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stat.setObject(1, entity.getName());
            stat.setObject(2, entity.getPath());
            stat.setObject(3, entity.getHash());
            stat.setObject(4, entity.getType());
            stat.setObject(5, entity.getDetail());
            stat.setObject(6,entity.isValid());
            entity.setUpdateTime(new Date());
            stat.setObject(7,entity.getUpdateTime());

            stat.execute();

            ResultSet rs = stat.getGeneratedKeys();
            if (rs.next()) {
                entity.setId(rs.getLong(1));
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

            StringBuilder where = new StringBuilder();
            while (it.hasNext()) {
                where.append(" AND ").append(store.explain(it.next()));
            }


            if (where.toString().startsWith(" AND ")) where = new StringBuilder(where.toString().replaceFirst(" AND ", " WHERE "));

            if (!StringUtils.isBlank(where.toString())) sql += where;

            sql += String.format(" LIMIT %s,%s", query.skip(), query.limit());
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
                module.setValid(rs.getBoolean("valid"));
                result.add(module);
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



    @Override
    public Module single(Query query) {
        query = query == null ? new Query() : query;

        query.limit(1);
        List<Module> result = select(query);

        return result!=null&&result.size() == 1 ? result.get(0) : null;
    }

    @Override
    public int delete(Query query) {
        assert query!=null;
        String sql = "DELETE FROM "+ MODULE_TABLE_NAME;

        Iterator<Condition> it = query.chain();
        StringBuilder where = new StringBuilder();
        while (it.hasNext()) {
            where.append(" AND ").append(store.explain(it.next()));
        }
        if (where.toString().startsWith(" AND ")) where = new StringBuilder(where.toString().replaceFirst(" AND ", " WHERE "));

        if (!StringUtils.isBlank(where.toString())) sql += where;


        try {
            PreparedStatement stat = connection.prepareStatement(sql);
            return stat.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;


    }

    @Override
    public int update(Module entity, Condition condition) {

        String sql = "UPDATE " + MODULE_TABLE_NAME + " SET name=?,path=?,hash=?,type=?,detail=?,valid=? ";


        String where = store.explain(condition);

        if (!StringUtils.isBlank(where)) sql += "WHERE " + where;

        System.out.println(sql);

        try {
            PreparedStatement stat = connection.prepareStatement(sql);
            stat.setObject(1, entity.getName());
            stat.setObject(2, entity.getPath());
            stat.setObject(3, entity.getHash());
            stat.setObject(4, entity.getType().name());
            stat.setObject(5, entity.getDetail());
            stat.setObject(6, entity.isValid());


            int count = stat.executeUpdate();
            entity.setUpdateTime(new Date());

            return count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

    @Override
    public void upsert(Query query, Module entity) {
        //String sql = "REPLACE "
    }

    @Override
    public long count(Query query) {
        String sql = "SELECT COUNT(*) FROM " + MODULE_TABLE_NAME;

        if (query != null) {
            Iterator<Condition> it = query.chain();

            String where = "";
            while (it.hasNext()) {
                where += " AND " + store.explain(it.next());
            }

            if (where.startsWith(" AND ")) where = where.replaceFirst(" AND ", " WHERE ");

            if (!StringUtils.isBlank(where)) sql += "WHERE " + where;
        }


        try {
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            return rs.next() ? rs.getLong(1) : 0;
        }catch (Exception e){
            return 0;
        }


    }


}
