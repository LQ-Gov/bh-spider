package com.charles.spider.store.sqlite;

import com.charles.spider.common.moudle.ModuleType;
import com.charles.spider.store.base.Criteria;
import com.charles.spider.store.base.Query;
import com.charles.spider.store.condition.Condition;
import com.charles.spider.store.entity.Module;
import com.charles.spider.store.service.ModuleService;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lq on 17-6-22.
 */
public class SQLiteModuleService implements ModuleService {
    private SQLiteStore store =null;
    private Connection connection;

    private String table = null;

    public SQLiteModuleService(SQLiteStore store) {
        this.store = store;
        this.connection = store.getConnection();

        this.table = "modules.db";
    }

    @Override
    public Module save(Module entity) {
        try {

            String sql = "INSERT ?(name,path,hash,type,updateTime) VALUE(?,?,?,?,?)";

            PreparedStatement stat = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stat.setObject(1, table);
            stat.setObject(2, entity.getName());
            stat.setObject(3, entity.getPath());
            stat.setObject(4, entity.getHash());
            stat.setObject(5, entity.getType());
            stat.setObject(6, entity.getUpdateTime());
            stat.execute();

            ResultSet rs = stat.getGeneratedKeys();
            if (rs.next()) entity.setId(rs.getLong(0));


        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }


        return entity;
    }

    @Override
    public List<Module> select(Query query) {

        String sql = "SELECT * FROM ? WHERE ";
        Iterator<Condition> it = query.chain();

        String where = "";
        while (it.hasNext()) {
            Condition cd = it.next();

            where += " AND " + store.explain(cd);
        }

        if (where.startsWith(" AND ")) where = where.replace(" AND ", "");
        sql += where;
        try {
            Statement stat = connection.createStatement();
            ResultSet rs = stat.executeQuery(sql);

            List<Module> result = new ArrayList<>();


            while (rs.next()){
                Module module = new Module();
                module.setName(rs.getString("name"));
                module.setPath(rs.getString("path"));
                module.setHash(rs.getString("hash"));
                module.setUpdateTime(rs.getDate("updateTime"));
                module.setType(ModuleType.valueOf(rs.getString("type")));
                module.setDetail(rs.getString("detail"));
                module.setId(rs.getLong("id"));
                result.add(module);
            }
            return result;
        }catch (SQLException e) {
            e.printStackTrace();
        }


        return null;
    }

    @Override
    public void delete(Module entity) {

    }

    @Override
    public void update(Module entity) {

    }
}
