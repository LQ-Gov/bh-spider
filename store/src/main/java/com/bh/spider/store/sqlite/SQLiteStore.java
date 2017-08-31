package com.bh.spider.store.sqlite;

import com.bh.spider.query.Query;
import com.bh.spider.query.annotation.StoreGenerationType;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.store.base.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by lq on 17-6-22.
 */
public class SQLiteStore extends AbstractStore {

    private Map<Class<?>, EntitiesBuilder> tableCaches = new HashMap<>();

    private Connection connection;
    private SQLiteQueryInterpreter interpreter;

    public SQLiteStore(Connection connection, Connection moduleConnection) {
        this.connection = connection;

        interpreter = new SQLiteQueryInterpreter();

    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() {

    }


    @Override
    public void connect() throws Exception {
        super.connect();
    }

    @Override
    protected void register0(EntitiesBuilder builder) throws Exception {

        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + builder.getTableName() + "(");

        //主键
        GeneratedField gen = builder.getGeneratedField();
        sql.append(gen.getStoreName()).append(" ");
        sql.append(typeConvert(gen.getType())).append(" PRIMARY KEY ");
        sql.append(gen.getStrategy() == StoreGenerationType.INCREMENT ? " AUTOINCREMENT," : ",");

        String[] fields = builder.getStoreFieldNames();

        for (String f : fields) {
            if (f.equals(gen.getStoreName())) continue;
            StoreField field = builder.getFieldMapping(f);
            sql.append(f).append(" ").append(typeConvert(field.getType()));
            if (field.isNotNull()) sql.append(" NOT NULL");
            sql.append(",");
        }
        sql.delete(sql.length() - 1, sql.length());
        sql.append(")");

        System.out.println(sql.toString());

        connection.prepareStatement(sql.toString()).execute();


    }

    @Override
    public Entity insert(Object o) {
        EntitiesBuilder builder = findBuilder(o.getClass());

        Entity entity = builder.toEntity(o);


        Map<String, Object> fields = entity.toMap();


        String[] names = (String[]) fields.keySet().toArray();

        Object[] values = fields.values().toArray();

        String sql = "INSERT INTO " + builder.getTableName() + "("
                + StringUtils.join(names, ',')
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
        EntitiesBuilder builder = findBuilder(cls);
        String sql = "SELECT COUNT(*) FROM " + builder.getTableName();


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
    public List<?> select(Class<?> cls, Query query) {

        EntitiesBuilder builder = findBuilder(cls);

        String sql = "SELECT * FROM " + builder.getTableName();

        String where = interpreter.explain(query);

        if (!StringUtils.isBlank(where)) sql += where;

        try {
            ResultSet rs = connection.prepareStatement(sql).executeQuery();

            List<Object> result = new LinkedList<>();

            String[] fields = builder.getStoreFieldNames();


            while (rs.next()) {

                Entity entity = builder.toEntity();
                for (String col : fields)
                    entity.set(col, rs.getObject(col));

                result.add(entity.toObject());
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Object single(Class<?> cls, Query query) {
        query = query == null ? new Query() : query;

        query.limit(1);
        List<?> result = select(cls, query);

        return result != null && result.size() == 1 ? result.get(0) : null;
    }

    @Override
    public int delete(Class<?> cls, Query query) {
        return 0;
    }

    @Override
    public int update(Object entity, Condition condition) {
        return 0;
    }


    private static String typeConvert(Class<?> cls) {
        if (ArrayUtils.contains(new Object[]{
                Integer.class, Integer.TYPE,
                Long.class, Long.TYPE}, cls))
            return "INTEGER";

        if (cls == String.class || Enum.class.isAssignableFrom(cls))
            return "TEXT";

        if (cls == Date.class)
            return "TIMESTAMP";

        return null;
    }


}
