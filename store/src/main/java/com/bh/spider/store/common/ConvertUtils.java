package com.bh.spider.store.common;

import com.bh.common.utils.Json;
import com.bh.spider.common.fetch.FetchMethod;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.impl.RequestImpl;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ConvertUtils {

    public static List<Request> convert(ResultSet rs) throws SQLException {
        List<Request> result = new LinkedList<>();
        while (rs.next()) {
            try {

                RequestImpl request = new RequestImpl(
                        rs.getLong("id"),
                        rs.getString("url"),
                        FetchMethod.valueOf(rs.getString("method")));

                request.setState(Request.State.valueOf(rs.getString("state")));

                request.setCreateTime(rs.getDate("create_time"));

                MapType mapType = Json.get().getTypeFactory().constructMapType(HashMap.class, String.class, String.class);
                request.headers().putAll(Json.get().readValue(StringUtils.defaultString(rs.getString("headers"), ""), mapType));
                request.extra().putAll(Json.get().readValue(StringUtils.defaultString(rs.getString("extra"), ""), mapType));
                result.add(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
