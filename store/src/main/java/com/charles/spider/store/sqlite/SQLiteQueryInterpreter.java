package com.charles.spider.store.sqlite;

import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.query.condition.Operator;
import com.charles.spider.query.condition.OrCondition;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;


import java.util.Date;
import java.util.Iterator;

public class SQLiteQueryInterpreter {

    public String explain(Query query) {
        if (query != null) {
            Iterator<Condition> it = query.chain();

            StringBuilder whereBuilder = new StringBuilder();
            while (it.hasNext()) {
                whereBuilder.append(" AND ").append(explain(it.next()));
            }

            whereBuilder.append(String.format(" LIMIT %s,%s", query.skip(), query.limit()));

            String where = whereBuilder.toString();
            if (where.startsWith(" AND ")) where = where.replaceFirst(" AND ", " WHERE ");


            return where;
        }

        return null;
    }

    public String explain(Condition condition) {


        StringBuilder builder = new StringBuilder();

        while (condition != null) {
            if (builder.length() > 0) {
                if (condition instanceof OrCondition)
                    builder.append(" OR ");
                else
                    builder.append(" AND ");
            }

            String key = condition.key();
            Operator operator = condition.operator();

            switch (operator) {
                case NOT:
                    builder.append(key).append("!=").append(valueToString(condition.value()));
                case IS:
                    builder.append(key).append("=").append(valueToString(condition.value()));
            }
            condition = condition.next();
        }

        return builder.toString();
    }

    private String valueToString(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return "'" + value + "'";


        if (value instanceof Date) return String.valueOf(((Date) value).getTime());

        if (ClassUtils.wrapperToPrimitive(value.getClass()) == null) {
            //判断数组
            //return value.toString();
        }

        return "'" + String.valueOf(value) + "'";

    }
}
