package com.bh.spider.scheduler.persist.sqlite;

import com.bh.spider.scheduler.persist.RequestService;
import com.bh.spider.fetch.HttpMethod;
import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.fetch.impl.FetchState;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.transfer.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class SQLiteRequestService implements RequestService<FetchRequest> {

    private ObjectMapper mapper = JsonFactory.get();
    private String tableName = "charles_spider_requests";

    private SQLiteQueryInterpreter interpreter = new SQLiteQueryInterpreter();

    private final JdbcTemplate template;

    public SQLiteRequestService(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void init() {
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
        template.execute(sql);
    }

    @Override
    public synchronized FetchRequest insert(FetchRequest o) {
        String sql = "INSERT INTO " + tableName + "(url,method,headers,params," +
                "extra,rule_id,hash,state,message,create_time,update_time) " +
                "VALUES(?,?,?,?,?,?,?,?,?,?,?)";

        Date now = new Date();
        o.setUpdateTime(now);
        o.setCreateTime(now);
        KeyHolder holder = new GeneratedKeyHolder();
        template.update(connection -> {
            try {
                PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                statement.setString(1, o.url().toString());
                statement.setString(2, o.method().toString());
                statement.setString(3, JsonFactory.get().writeValueAsString(o.headers()));
                statement.setString(4, JsonFactory.get().writeValueAsString(o.params()));
                statement.setString(5, JsonFactory.get().writeValueAsString(o.extra()));
                statement.setObject(6, o.getRuleId());
                statement.setString(7, o.hash());
                statement.setString(8, o.getState().toString());
                statement.setString(9, o.getMessage());
                statement.setObject(10, o.getCreateTime());
                statement.setObject(11, o.getUpdateTime());
                return statement;
            } catch (Exception ignored) {
                return null;
            }
        }, holder);

        o.setId(holder.getKey().longValue());
        return o;
    }

    @Override
    public long count(Query query) {
        String sql = interpreter.explain("SELECT COUNT(*) FROM " + tableName, query);
        return template.queryForObject(sql, Long.class);
    }

    @Override
    public List<FetchRequest> select(Query query) {
        String sql = interpreter.explain("SELECT * FROM " + tableName, query);

        return template.query(sql, new Mapper());
    }

    @Override
    public FetchRequest single(Query query) {
        query.limit(1);

        List<FetchRequest> list = select(query);

        return list == null || list.isEmpty() ? null : list.get(0);
    }

    @Override
    public synchronized int delete(Query query) {
        String sql = interpreter.explain("DELETE FROM " + tableName, query);
        return template.update(sql);
    }

    @Override
    public synchronized int update(FetchRequest o, Condition condition) {
        Date now = new Date();
        o.setUpdateTime(now);
        String sql = "UPDATE " + tableName + " SET headers=?,params=?,extra=?,state=?,message=?,update_time=?";
        sql = interpreter.explain(sql, condition);

        return template.update(sql, preparedStatement -> {
            try {
                preparedStatement.setString(1, mapper.writeValueAsString(o.headers()));
                preparedStatement.setString(2, mapper.writeValueAsString(o.params()));
                preparedStatement.setString(3, mapper.writeValueAsString(o.extra()));
                preparedStatement.setString(4, o.getState().toString());
                preparedStatement.setString(5, o.getMessage());
                preparedStatement.setObject(6, o.getUpdateTime());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public synchronized int updateState(FetchState state, String message, Condition condition) {

        String sql = "UPDATE " + tableName + " SET state=?,message=?,update_time=?";
        sql = interpreter.explain(sql, condition);

        return template.update(sql, preparedStatement -> {
            preparedStatement.setString(1, state.toString());
            preparedStatement.setString(2, message);
            preparedStatement.setObject(3, new Date());

        });

    }


    private class Mapper implements RowMapper<FetchRequest> {

        @Override
        public FetchRequest mapRow(ResultSet resultSet, int i) throws SQLException {
            try {
                String url = resultSet.getString("url");
                HttpMethod method = HttpMethod.valueOf(resultSet.getString("method"));
                FetchRequest req = new FetchRequest(url, method);
                req.setId(resultSet.getLong("id"));
                req.headers().putAll(mapper.readValue(resultSet.getString("headers"), JsonFactory.mapType(String.class, String.class)));
                req.params().putAll(mapper.readValue(resultSet.getString("params"), JsonFactory.mapType(String.class, Object.class)));
                req.extra().putAll(mapper.readValue(resultSet.getString("extra"), JsonFactory.mapType(String.class, Object.class)));
                req.setState(FetchState.valueOf(resultSet.getString("state")));
                req.setRuleId(resultSet.getString("rule_id"));
                req.setCreateTime(resultSet.getDate("create_time"));
                return req;


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
