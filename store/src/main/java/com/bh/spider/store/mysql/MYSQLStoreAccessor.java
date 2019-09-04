package com.bh.spider.store.mysql;

import com.bh.common.utils.Json;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.store.base.StoreAccessor;
import com.bh.spider.store.common.ConvertUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.StringUtils;

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
                "  `headers` JSON NULL COMMENT 'http header'," +
                "  `params` JSON NULL COMMENT '请求传递的参数'," +
                "  `extra` JSON NULL COMMENT '传递参数（不参与请求）'," +
                "  `hash` varchar(300) NOT NULL COMMENT 'url hash值'," +
                "  `rule_id` bigint(11) NULL COMMENT '关联的rule'," +
                "  `state` varchar(10) NOT NULL COMMENT '状态'," +
                "  `code` integer(2) COMMENT '返回状态'," +
                "  `create_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
                "  `update_time` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'," +
                "  PRIMARY KEY (`id`)," +
                "  UNIQUE INDEX `uniq_hash_index`(`hash`) USING BTREE" +
                ") ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_unicode_ci;");
    }


    @Override
    public boolean insert(Request request, long ruleId) {
        try {
            runner.update("INSERT bh_spider_url（id,url,method,headers,params,extra,hash,rule_id,state） " +
                            "VALUES(?,?,?,?,?,?,?,?,?,?) ",
                    request.id(), request.url(), request.method(), Json.get().writeValueAsString(request.headers())
                    , null, Json.get().writeValueAsString(request.extra()), request.hash(), ruleId, request.state());
        } catch (SQLException | JsonProcessingException e) {
            e.printStackTrace();
        }


        return false;
    }

    @Override
    public boolean save(Request request, long ruleId) {
        try {
            String headers = Json.get().writeValueAsString(request.headers());
            String params = null;
            String extra = Json.get().writeValueAsString(request.extra());
            int returnValue = runner.update("INSERT bh_spider_url(id,url,method,headers,params,extra,hash,rule_id,state) VALUES(?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE " +
                            "headers=?,params=?,extra=?,rule_id=?,state=?",
                    request.id(), request.url().toString(), request.method().toString(), headers, params, extra, request.hash(), ruleId, request.state().name(),
                    headers, params, extra, ruleId, request.state().name());


            //如果是1 则是insert，2是update
            return returnValue == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void update(long ruleId, Long[] reIds, Request.State state) {
        try {
            String sql = String.format("UPDATE bh_spider_url SET state=? WHERE rule_id=? AND id in (%s)", StringUtils.join(reIds, ","));
            runner.update(sql, state.name(), ruleId);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void update(long id, Integer code, Request.State state, String message) {
        try {
            int res = runner.update("UPDATE bh_spider_url SET code=?,state=? WHERE id=?", code, state.name(), id);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void reset(long ruleId) {
        try {
            int res = runner.update("UPDATE bh_spider_url SET rule_id=0,state=? WHERE rule_id=?", Request.State.ASSIGNING.name(), ruleId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Request> find(long ruleId, Request.State state, long size) {

        try {
            return find(ruleId, state, 0, size);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Request> find(long ruleId, Request.State state, long offset, long size) {
        try {
            return runner.query("SELECT * FROM bh_spider_url WHERE rule_id=? AND state=? LIMIT ?,?"
                    , ConvertUtils::convert, ruleId, state.name(), offset, size);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long count(Long ruleId, Request.State state) {
        String sql = "SELECT COUNT(0) FROM bh_spider_url";
        if (state != null || ruleId != null)
            sql += " WHERE ";
        if (ruleId != null) {
            sql += " rule_id=" + ruleId;
        }

        if (state != null) {
            if (ruleId != null) sql += " AND ";
            sql += " state='" + state.name() + "'";
        }

        try {
            return runner.query(sql, new ScalarHandler<>());
        } catch (Exception e) {
            return -1;
        }
    }
}
