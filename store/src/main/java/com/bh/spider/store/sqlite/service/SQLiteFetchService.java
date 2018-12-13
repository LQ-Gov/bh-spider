package com.bh.spider.store.sqlite.service;

import com.bh.spider.fetch.FetchMethod;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.RequestImpl;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.fetch.impl.RequestBuilder;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.store.service.FetchService;
import com.bh.spider.store.sqlite.SQLiteIndex;
import com.bh.spider.store.sqlite.SQLiteQueryInterpreter;
import com.bh.spider.store.sqlite.SQLiteStore;
import com.bh.spider.transfer.JsonFactory;
import com.bh.spider.rule.Rule;
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
    public FetchState insert(RequestImpl req, Rule rule) {
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

    @Override
    public RequestImpl insert(RequestImpl o) {
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
    public List<RequestImpl> select(Query query) {
        String sql = interpreter.explain("SELECT * FROM " + tableName, query);

        try {
            ResultSet result = store.connection().prepareStatement(sql).executeQuery();

            List<RequestImpl> collection = new LinkedList<>();
            while (result.next()) {

                String url = result.getString("url");
                FetchMethod method = FetchMethod.valueOf(result.getString("method"));

                FetchState state = new FetchState();
                state.setState(Request.State.valueOf(result.getString("state")));
                state.setMessage(result.getString("message"));
                state.setUpdateTime(result.getDate("update_time"));

                RequestImpl request = (RequestImpl) RequestBuilder.create(url, method)
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
    public RequestImpl single(Query query) {

        query.limit(1);

        List<RequestImpl> list = select(query);

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
    public int update(RequestImpl o, Condition condition) {
        throw new Error("not support this function");
    }

}
