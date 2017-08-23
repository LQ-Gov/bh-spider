package com.charles.spider.scheduler.persist.sqlite;

import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.scheduler.persist.Service;
import com.charles.spider.transfer.entity.Module;
import com.charles.spider.transfer.entity.ModuleType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SQLiteModuleService implements Service<Module> {
    private String tableName = "charles_spider_modules";

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
    public Module insert(Module o) {

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
    public List<Module> select(Query query) {

        String sql = "SELECT * FROM " + tableName;


        String where = interpreter.explain(query);


        if (!StringUtils.isBlank(where)) sql += " " + where;

        return template.query(sql, new Mapper());
    }

    @Override
    public Module single(Query query) {

        query.limit(1);
        List<Module> list = select(query);

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
    public int update(Module o, Condition condition) {


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

    private class Mapper implements RowMapper<Module> {

        @Override
        public Module mapRow(ResultSet resultSet, int i) throws SQLException {
            Module module = new Module();
            module.setId(resultSet.getLong("id"));
            module.setName(resultSet.getString("name"));
            module.setPath(resultSet.getString("path"));
            module.setHash(resultSet.getString("hash"));
            module.setState(Module.State.valueOf(resultSet.getString("state")));
            module.setType(ModuleType.valueOf(resultSet.getString("type")));
            module.setDetail(resultSet.getString("detail"));
            module.setUpdateTime(resultSet.getDate("update_time"));
            return module;
        }
    }
}
