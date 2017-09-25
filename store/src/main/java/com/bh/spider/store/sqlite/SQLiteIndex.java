package com.bh.spider.store.sqlite;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLiteIndex {

    private String indexName;
    private String[] fields;
    private String tableName;

    private Connection connection;

    public SQLiteIndex(Connection connection, String tableName, String indexName, String... fields) {
        this.connection = connection;
        this.tableName = tableName;
        this.indexName = indexName;
        this.fields = fields;
    }


    public boolean exists() {
        String sql = "SELECT COUNT(*) FROM sqlite_master WHERE type='index' AND name='" + indexName + "'";
        try {
            ResultSet resultSet = connection.prepareStatement(sql).executeQuery();

            return resultSet.next() && resultSet.getInt(1) > 0;


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean create() throws SQLException {
        String sql = "CREATE INDEX " + indexName + " ON " + tableName + "(" + StringUtils.join(fields, ',') + ")";

        return connection.prepareStatement(sql).execute();
    }

}
