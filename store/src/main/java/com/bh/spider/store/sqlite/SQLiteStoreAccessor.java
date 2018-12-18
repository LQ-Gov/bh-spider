package com.bh.spider.store.sqlite;

import com.bh.spider.fetch.FetchMethod;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.fetch.impl.RequestImpl;
import com.bh.spider.store.base.StoreAccessor;
import com.bh.spider.transfer.JsonFactory;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.commons.lang3.StringUtils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SQLiteStoreAccessor implements StoreAccessor {
    private SQLiteStore store;
    private final static String TABLE_NAME="bh_spider_url";

    public SQLiteStoreAccessor(SQLiteStore store) {
        this.store = store;
    }


    public void init() throws SQLException {

        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" +
                " id INTEGER PRIMARY KEY," +
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

        indexes[0] = new SQLiteIndex(store.connection(), TABLE_NAME, "rule_index", "rule_id");
        indexes[1] = new SQLiteIndex(store.connection(), TABLE_NAME, "state_index", "state");
        indexes[2] = new SQLiteIndex(store.connection(), TABLE_NAME, "hash_index", "hash");

        for (SQLiteIndex index : indexes) {
            if (!index.exists()) index.create();
        }
    }

    @Override
    public boolean insert(Request req,long ruleId) {

        String sql = "INSERT INTO " + TABLE_NAME + "(id,url,method,headers,params," +
                "extra,rule_id,hash,state,message,create_time,update_time) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try {

            //FetchState state = FetchState.queue();
            PreparedStatement statement = store.connection().prepareStatement(sql);
            int pos = 1;
            statement.setLong(pos++, req.id());
            statement.setString(pos++, req.url().toString());
            statement.setString(pos++, req.method().toString());
            statement.setString(pos++, JsonFactory.get().writeValueAsString(req.headers()));
            statement.setString(pos++, null);
            statement.setString(pos++, JsonFactory.get().writeValueAsString(req.extra()));
            statement.setLong(pos++, ruleId);
            statement.setString(pos++, req.hash());
            statement.setString(pos++, Request.State.QUEUE.name());
            statement.setString(pos++, null);
            statement.setObject(pos++, req.createTime());
            statement.setObject(pos++, null);

            return statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void update(long ruleId, Long[] reIds, Request.State state) {
        String sql = "UPDATE " + TABLE_NAME + " SET state=? WHERE rule_id=? AND id in (?)";
        try {
            PreparedStatement statement = store.connection().prepareStatement(sql);
            statement.setLong(1, ruleId);
            statement.setString(2, state.name());
            statement.setArray(3, store.connection().createArrayOf("INTEGER", reIds));

            statement.execute();
        } catch (Exception e) {

        }
    }

    @Override
    public List<Request> find(long ruleId, Request.State state, long size) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE rule_id=? AND state=? LIMIT ?";

        try {
            PreparedStatement statement = store.connection().prepareStatement(sql);
            statement.setLong(1,ruleId);
            statement.setString(2,state.name());
            statement.setLong(3,size);
            ResultSet rs = statement.executeQuery();

            List<Request> result =new LinkedList<>();
            while (rs.next()) {

                Request request = new RequestImpl(
                        rs.getLong("id"),
                        rs.getString("url"),
                        FetchMethod.valueOf(rs.getString("method")));


                MapType mapType = JsonFactory.get().getTypeFactory().constructMapType(HashMap.class, String.class, String.class);
                request.headers().putAll(JsonFactory.get().readValue(StringUtils.defaultString(rs.getString("headers"),""),mapType));
                request.extra().putAll(JsonFactory.get().readValue(StringUtils.defaultString(rs.getString("extra"),""),mapType));
                result.add(request);
            }
            return result;

        }catch (Exception e){
        }
        return Collections.emptyList();
    }




}
