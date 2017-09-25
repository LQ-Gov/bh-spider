package com.bh.spider.store.sqlite;

import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.query.condition.Operator;
import com.bh.spider.query.condition.OrCondition;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.Collection;
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
                    break;
                case IS:
                    builder.append(key).append("=").append(valueToString(condition.value()));
                    break;
                case IN: {
                    String val = parseCollection(condition.value());
                    if (val != null)
                        builder.append(key).append(" IN ").append(val);
                }
                break;
            }
            condition = condition.next();
        }

        return builder.toString();
    }


    public String explain(String prefix, Query query) {
        String where = explain(query);
        if (StringUtils.isNoneBlank(where)) prefix += " " + where;

        return prefix;
    }


    public String explain(String prefix, Condition condition) {
        String where = explain(condition);


        if (!StringUtils.isBlank(where)) prefix += " WHERE " + where;

        return prefix;
    }

    private String valueToString(Object value) {
        if (value == null) return "null";
        if (value instanceof String) return "'" + value + "'";

        if (value.getClass().isPrimitive()) {

            Class<?> wrapperClass = ClassUtils.primitiveToWrapper(value.getClass());
            if (Number.class.isAssignableFrom(wrapperClass)) {
                return value.toString();
            }
        }


        if (value instanceof Date) return String.valueOf(((Date) value).getTime());

        return "'" + String.valueOf(value) + "'";

    }

    private String parseCollection(Object o) {
        StringBuilder builder = new StringBuilder("(");
        Class<?> cls = o.getClass();
        if (cls.isArray()) {
            int len = Array.getLength(o);
            for (int i = 0; i < len; i++)
                builder.append(valueToString(Array.get(o, i))).append(",");
        } else if (Collection.class.isAssignableFrom(cls)) {
            Collection collection = (Collection) o;
            for (Object it : collection) {
                builder.append(valueToString(it)).append(",");
            }
        }
        char c = builder.charAt(builder.length() - 1);
        if (c == ',')
            builder.delete(builder.length() - 1, builder.length());
        else if (c == '(')
            return null;
        builder.append(")");

        return builder.toString();

    }
}
