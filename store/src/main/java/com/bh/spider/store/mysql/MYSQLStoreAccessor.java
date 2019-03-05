package com.bh.spider.store.mysql;

import com.bh.common.utils.Json;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.impl.RequestImpl;
import com.bh.spider.store.base.StoreAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class MYSQLStoreAccessor implements StoreAccessor {
    private DataSource dataSource;

    private QueryRunner runner;

    public MYSQLStoreAccessor(DataSource dataSource) {
        this.dataSource = dataSource;
        this.runner = new QueryRunner(dataSource);
    }

    public void init() throws SQLException {
        
        runner.execute("CREATE TABLE IF NOT EXISTS `bh_spider_url`  (" +
                "  `id` bigint(0) NOT NULL COMMENT 'ID'," +
                "  `url` varchar(8182) NOT NULL COMMENT 'URL'," +
                "  `method` varchar(6) NOT NULL COMMENT 'method'," +
                "  `headers` varchar(3000) NULL COMMENT 'http header'," +
                "  `params` varchar(3000) NULL COMMENT '请求传递的参数'," +
                "  `extra` varchar(2000) NULL COMMENT '传递参数（不参与请求）'," +
                "  `hash` varchar(300) NOT NULL COMMENT 'url hash值'," +
                "  `rule_id` int(11) NULL COMMENT '关联的rule'," +
                "  `state` varchar(10) NOT NULL COMMENT '状态'," +
                "  `code` integer(2) NOT NULL COMMENT '返回状态'," +
                "  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                "  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                "  PRIMARY KEY (`id`)" +
                ");");

    }


    @Override
    public boolean insert(Request request, long ruleId) {
        try {
            runner.update("INSERT bh_spider_url（id,url,method,headers,params,extra,hash,rule_id,state） " +
                            "VALUES(?,?,?,?,?,?,?,?,?,?)",
                    request.id(), request.url(), request.method(), Json.get().writeValueAsString(request.headers())
                    , null, Json.get().writeValueAsString(request.extra()), request.hash(), ruleId, Request.State.QUEUE);
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public void update(long ruleId, Long[] reIds, Request.State state) {
        try {
            runner.update("UPDATE bh_spider_url SET state=? WHERE rule_id=? AND id IN ?",state,ruleId,reIds);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void update(long id, Integer code, Request.State state, String message) {
        try {
            int res = runner.update("UPDATE bh_spider_url SET code=?,state=? WHERE id=?",code,state,id);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public List<Request> find(long ruleId, Request.State state, long size) {

        try {
            return find(ruleId,state,0,size);
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Request> find(long ruleId, Request.State state, long offset, long size) {
        try {
            return runner.query("SELECT * FROM bh_spider_url WHERE rule_id=? AND state=? LIMIT ?,?"
                    , new BeanListHandler<>(RequestImpl.class), ruleId, state, offset, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long count(long ruleId, Request.State state) {
        try{
            long count = runner.query("SELECT COUNT(0) FROM bh_spider_url WHERE rule_id=? AND state=?"
                    ,new ScalarHandler<>(),ruleId,state);
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
