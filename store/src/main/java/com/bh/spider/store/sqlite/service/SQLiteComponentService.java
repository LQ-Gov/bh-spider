package com.bh.spider.store.sqlite.service;

import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.store.service.Service;
import com.bh.spider.store.sqlite.SQLiteIndex;
import com.bh.spider.store.sqlite.SQLiteQueryInterpreter;
import com.bh.spider.store.sqlite.SQLiteStore;
import com.bh.spider.transfer.entity.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


public class SQLiteComponentService implements Service<Component> {
    private final static SQLiteQueryInterpreter interpreter = new SQLiteQueryInterpreter();

    private SQLiteStore store;
    private String tableName;

    public SQLiteComponentService(SQLiteStore store, String tableName) {
        this.store = store;
        this.tableName = tableName;
    }

    public void init() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "path TEXT," +
                "hash TEXT," +
                "type TEXT," +
                "state TEXT," +
                "detail TEXT," +
                "update_time TIMESTAMP default CURRENT_TIMESTAMP)";


        store.connection().prepareStatement(sql).execute();

        SQLiteIndex[] indexes = new SQLiteIndex[1];
        indexes[0] = new SQLiteIndex(store.connection(), tableName, "component_name_index", "name");
        for (SQLiteIndex index : indexes) {
            if (!index.exists()) index.create();
        }
    }

    @Override
    public Component insert(Component o) {

        String sql = "INSERT INTO " + tableName + "(name,path,hash,type,detail,state,update_time) VALUES(?,?,?,?,?,?,?)";


        try {
            PreparedStatement statement = store.connection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, o.getName());
            statement.setString(2, o.getPath());
            statement.setString(3, o.getHash());
            statement.setString(4, o.getType().toString());
            statement.setString(5, o.getDetail());
            statement.setString(6, o.getState().toString());
            statement.setObject(7, o.getUpdateTime());

            if (statement.execute()) {
                ResultSet result = statement.getGeneratedKeys();
                o.setId(result.next() ? result.getLong(1) : o.getId());
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public long count(Query query) {

        String sql = interpreter.explain("SELECT COUNT(*) FROM " + tableName, query);

        try {
            ResultSet result = store.connection().prepareStatement(sql).executeQuery();
            return result.next() ? result.getLong(1) : 0L;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public List<Component> select(Query query) {

        String sql = interpreter.explain("SELECT * FROM " + tableName, query);


        try {
            ResultSet result = store.connection().prepareStatement(sql).executeQuery();

            List<Component> components = new LinkedList<>();
            while (result.next()) {
                Component component = new Component();
                component.setId(result.getLong("id"));
                component.setName(result.getString("name"));
                component.setPath(result.getString("path"));
                component.setHash(result.getString("hash"));
                component.setState(Component.State.valueOf(result.getString("state")));
                component.setType(Component.Type.valueOf(result.getString("type")));
                component.setDetail(result.getString("detail"));
                component.setUpdateTime(result.getDate("update_time"));

                components.add(component);
            }

            return components;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Component single(Query query) {

        query.limit(1);
        List<Component> list = select(query);

        return list == null || list.isEmpty() ? null : list.get(0);

    }

    @Override
    public int delete(Query query) {

        String sql = interpreter.explain("DELETE FROM " + tableName, query);

        try {
            return store.connection().prepareStatement(sql).executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;

    }

    @Override
    public int update(Component o, Condition condition) {


        String sql = "UPDATE " + tableName + " SET name=?,path=?,hash=?,type=?,detail=?,update_time=? ";

        sql = interpreter.explain(sql, condition);

        try {
            PreparedStatement statement = store.connection().prepareStatement(sql);
            statement.setString(1, o.getName());
            statement.setString(2, o.getPath());
            statement.setString(3, o.getHash());
            statement.setString(4, o.getType().toString());
            statement.setString(5, o.getDetail());
            statement.setObject(6, o.getUpdateTime());

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
