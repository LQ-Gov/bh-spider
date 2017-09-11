package com.bh.spider.scheduler.persist.sqlite;

import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.scheduler.persist.Service;
import com.bh.spider.transfer.entity.Component;
import com.bh.spider.transfer.entity.ModuleType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SQLiteModuleService implements Service<Component> {
    private String tableName = "bh_spider_modules";

    private SQLiteQueryInterpreter interpreter = new SQLiteQueryInterpreter();

    private final JdbcTemplate template;

    public SQLiteModuleService(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public void init() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "path TEXT," +
                "hash TEXT," +
                "type TEXT," +
                "state TEXT," +
                "detail TEXT," +
                "update_time TIMESTAMP default CURRENT_TIMESTAMP)";
        template.execute(sql);
    }

    @Override
    public Component insert(Component o) {

        String sql = "INSERT INTO " + tableName + "(name,path,hash,type,detail,state,update_time) VALUES(?,?,?,?,?,?,?)";


        KeyHolder holder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, o.getName());
            statement.setString(2, o.getPath());
            statement.setString(3, o.getHash());
            statement.setString(4, o.getType().toString());
            statement.setString(5, o.getDetail());
            statement.setString(6, o.getState().toString());
            statement.setObject(7, o.getUpdateTime());

            return statement;

        }, holder);

        o.setId(holder.getKey().longValue());
        return o;
    }

    @Override
    public long count(Query query) {
        String sql = "SELECT COUNT(*) FROM " + tableName;


        String where = interpreter.explain(query);


        if (!StringUtils.isBlank(where)) sql += " " + where;

        return template.queryForObject(sql, Long.class);
    }

    @Override
    public List<Component> select(Query query) {

        String sql = "SELECT * FROM " + tableName;


        String where = interpreter.explain(query);


        if (!StringUtils.isBlank(where)) sql += " " + where;

        return template.query(sql, new Mapper());
    }

    @Override
    public Component single(Query query) {

        query.limit(1);
        List<Component> list = select(query);

        return list == null || list.isEmpty() ? null : list.get(0);

    }

    @Override
    public int delete(Query query) {

        String sql = "DELETE FROM " + tableName;

        String where = interpreter.explain(query);

        if (!StringUtils.isBlank(where)) sql += " " + where;

        return template.update(sql);

    }

    @Override
    public int update(Component o, Condition condition) {


        String sql = "UPDATE " + tableName + " SET name=?,path=?,hash=?,type=?,detail=?,update_time=? ";

        sql = interpreter.explain(sql, condition);

        return template.update(sql, statement -> {
            statement.setString(1, o.getName());
            statement.setString(2, o.getPath());
            statement.setString(3, o.getHash());
            statement.setString(4, o.getType().toString());
            statement.setString(5, o.getDetail());
            statement.setObject(6, o.getUpdateTime());
        });


    }

    private class Mapper implements RowMapper<Component> {

        @Override
        public Component mapRow(ResultSet resultSet, int i) throws SQLException {
            Component component = new Component();
            component.setId(resultSet.getLong("id"));
            component.setName(resultSet.getString("name"));
            component.setPath(resultSet.getString("path"));
            component.setHash(resultSet.getString("hash"));
            component.setState(Component.State.valueOf(resultSet.getString("state")));
            component.setType(ModuleType.valueOf(resultSet.getString("type")));
            component.setDetail(resultSet.getString("detail"));
            component.setUpdateTime(resultSet.getDate("update_time"));
            return component;
        }
    }
}
