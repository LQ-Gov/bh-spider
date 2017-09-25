package com.bh.spider.store.sqlite.service;

import com.bh.spider.fetch.HttpMethod;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.fetch.impl.RequestBuilder;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.store.service.FetchService;
import com.bh.spider.store.sqlite.SQLiteIndex;
import com.bh.spider.store.sqlite.SQLiteQueryInterpreter;
import com.bh.spider.store.sqlite.SQLiteStore;
import com.bh.spider.transfer.JsonFactory;
import com.bh.spider.transfer.entity.Rule;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class SQLiteFetchService implements FetchService {

    private final static SQLiteQueryInterpreter interpreter = new SQLiteQueryInterpreter();

    private final static ObjectMapper mapper = JsonFactory.get();
    private SQLiteStore store;
    private String tableName;

    public SQLiteFetchService(SQLiteStore store, String tableName) {
        this.store = store;
        this.tableName = tableName;
    }


    @Override
    public FetchState insert(FetchRequest req, Rule rule) {
        String sql = "INSERT INTO " + tableName + "(url,method,headers,params," +
                "extra,rule_id,hash,state,message,create_time,update_time) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try {

            FetchState state = FetchState.queue();
            PreparedStatement statement = store.connection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, req.url().toString());
            statement.setString(2, req.method().toString());
            statement.setString(3, JsonFactory.get().writeValueAsString(req.headers()));
            statement.setString(4, null);
            statement.setString(5, JsonFactory.get().writeValueAsString(req.extra()));
            statement.setObject(6, rule == null ? null : rule.getId());
            statement.setString(7, req.hash());
            statement.setString(8, state.getState().toString());
            statement.setString(9, null);
            statement.setObject(10, req.createTime());
            statement.setObject(11, null);

            statement.execute();

            ResultSet result = statement.getGeneratedKeys();
            long id = result.next() ? result.getLong(1) : req.id();

            new RequestBuilder(req).setId(id).setState(state).build();
            return state;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int update(long id, FetchState state) {
        Condition condition = Condition.where("id").is(id);
        return update(condition, state);
    }

    @Override
    public int update(Condition condition, FetchState state) {
        String sql = interpreter.explain("UPDATE " + tableName + " SET update_time=?,state=?,message=?", condition);
        try {
            state.setUpdateTime(new Date());
            PreparedStatement statement = store.connection().prepareStatement(sql);
            statement.setObject(1, state.getUpdateTime());
            statement.setString(2, state.getState().toString());
            statement.setString(3, state.getMessage());

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void init() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "url TEXT," +
                "method TEXT," +
                "headers TEXT," +
                "params TEXT," +
                "extra TEXT," +
                "hash TEXT," +
                "rule_id TEXT," +
                "state TEXT," +
                "message TEXT," +
                "create_time TIMESTAMP default CURRENT_TIMESTAMP," +
                "update_time TIMESTAMP)";

        store.connection().prepareStatement(sql).execute();

        SQLiteIndex[] indexes = new SQLiteIndex[3];

        indexes[0] = new SQLiteIndex(store.connection(), tableName, "request_rule_index", "rule_id");
        indexes[1] = new SQLiteIndex(store.connection(), tableName, "request_state_index", "state");
        indexes[2] = new SQLiteIndex(store.connection(), tableName, "request_hash_index", "hash");

        for (SQLiteIndex index : indexes) {
            if (!index.exists()) index.create();
        }
    }

    @Override
    public FetchRequest insert(FetchRequest o) {
        return null;
    }


    @Override
    public long count(Query query) {
        String sql = interpreter.explain("SELECT COUNT(*) FROM " + tableName, query);

        try {
            ResultSet result = store.connection().prepareStatement(sql).executeQuery();
            return result.next() ? result.getLong(1) : 0;
        } catch (SQLException ignored) {
        }
        return 0;
    }

    @Override
    public List<FetchRequest> select(Query query) {
        String sql = interpreter.explain("SELECT * FROM " + tableName, query);

        try {
            ResultSet result = store.connection().prepareStatement(sql).executeQuery();

            List<FetchRequest> collection = new LinkedList<>();
            while (result.next()) {

                String url = result.getString("url");
                HttpMethod method = HttpMethod.valueOf(result.getString("method"));

                FetchState state = new FetchState();
                state.setState(Request.State.valueOf(result.getString("state")));
                state.setMessage(result.getString("message"));
                state.setUpdateTime(result.getDate("update_time"));

                FetchRequest request = (FetchRequest) RequestBuilder.create(url, method)
                        .setId(result.getLong("id"))
                        .setCreateTime(result.getDate("create_time"))
                        .setState(state)
                        .build();


                request.headers().putAll(mapper.readValue(result.getString("headers"), JsonFactory.mapType(String.class, String.class)));

                request.extra().putAll(mapper.readValue(result.getString("extra"), JsonFactory.mapType(String.class, Object.class)));

                collection.add(request);
            }

            return collection;

        } catch (SQLException | MalformedURLException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FetchRequest single(Query query) {

        query.limit(1);

        List<FetchRequest> list = select(query);

        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int delete(Query query) {
        String sql = interpreter.explain("DELETE FROM " + tableName, query);
        try {
            ResultSet result = store.connection().prepareStatement(sql).executeQuery();
            return result.getInt(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int update(FetchRequest o, Condition condition) {
        throw new Error("not support this function");
    }

}
