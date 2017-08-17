package com.charles.spider.store.sqlite;

import com.charles.spider.query.Query;
import com.charles.spider.query.annotation.GeneratedKey;
import com.charles.spider.query.annotation.Table;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.store.base.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lq on 17-6-22.
 */
public class SQLiteStore implements Store {

    private Map<Class<?>, EntitiesBuilder> tableCaches = new HashMap<>();

    private Connection connection;
    private SQLiteQueryInterpreter interpreter;
    private boolean initialized = false;

    public SQLiteStore(Connection connection, Connection moduleConnection) {
        this.connection = connection;

        interpreter = new SQLiteQueryInterpreter();

    }

    public Connection getConnection() {
        return connection;
    }


    private EntitiesBuilder findEntityBuilder(Class<?> cls) {

        EntitiesBuilder builder = tableCaches.get(cls);


        if (builder == null) throw new RuntimeException("the table not register");

        return builder;
    }


    @Override
    public synchronized void init() {
        if (initialized) {

            initialized = true;
        }

    }

    @Override
    public synchronized void register(Class<?> cls, String table) {
        if (initialized) throw new RuntimeException("you must register before initialized");

        if (tableCaches.containsKey(cls) || tableCaches.containsValue(table))
            throw new RuntimeException("the table is registered");

        if (StringUtils.isBlank(table)) {
            Table tableAnnotation = cls.getAnnotation(Table.class);
            if (tableAnnotation == null || StringUtils.isBlank(tableAnnotation.value()))
                throw new RuntimeException("you must special a table name from " + cls.getName());
            table = tableAnnotation.value();
        }


        EntitiesBuilder builder = new EntitiesBuilder(table, cls);

        tableCaches.put(cls, builder);
    }

    @Override
    public Entity insert(Entity entity) {
        EntitiesBuilder builder = findEntityBuilder(entity.getType());

        Field[] fields = entity.getFields();


        String[] names = new String[fields.length];
        Object[] values = new Object[fields.length];
        for (int i = 0; i < fields.length; i++) {
            names[i] = fields[i].getName();
            values[i] = fields[i].getValue();
        }

        String sql = "INSERT INTO " + table + "(" + StringUtils.join(names, ',')
                + ") VALUES(" + StringUtils.repeat('?', names.length) + ")";


        try {
            PreparedStatement stat = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i <= values.length; i++)
                stat.setObject(i, values[i]);

            ResultSet rs = stat.getGeneratedKeys();
            if (rs.next()) {
                entity.setGeneratedKey(rs.getObject(1));
            }
            return entity;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public long count(Class<?> cls, Query query) {
        String sql = "SELECT COUNT(*) FROM " + getTable(cls);


        String where = interpreter.explain(query);


        if (!StringUtils.isBlank(where)) sql += where;


        try {
            ResultSet rs = connection.prepareStatement(sql).executeQuery();
            return rs.next() ? rs.getLong(1) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public <T> List<T> select(Class<T> cls, Query query) {

        String sql = "SELECT * FROM " + getTable(cls);

        String where = interpreter.explain(query);

        if (!StringUtils.isBlank(where)) sql += where;

        try {
            ResultSet rs = connection.prepareStatement(sql).executeQuery();

            List<T> result = new LinkedList<>();

            ResultSetMetaData meta = rs.getMetaData();
            String[] columns = new String[meta.getColumnCount()];
            for (int i = 0; i < columns.length; i++) columns[i] = meta.getColumnName(i);

            while (rs.next()) {
                Entity entity = new Entity(cls);
                for (String col : columns)
                    entity.set(col, rs.getObject(col));

                result.add((T) entity.toObject());
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public <T> T single(Class<T> cls, Query query) {
        query = query == null ? new Query() : query;

        query.limit(1);
        List<T> result = select(cls, query);

        return result != null && result.size() == 1 ? result.get(0) : null;
    }

    @Override
    public int delete(Class<?> cls, Query query) {
        return 0;
    }

    @Override
    public int update(Entity entity, Condition condition) {
        return 0;
    }


}
