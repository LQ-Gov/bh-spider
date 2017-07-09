package com.charles.spider.store.sqlite;

import com.charles.spider.query.condition.Condition;
import com.charles.spider.query.condition.Operator;
import com.charles.spider.query.condition.OrCondition;
import org.apache.commons.lang3.ClassUtils;

import java.util.Date;

/**
 * Created by lq on 17-6-23.
 */
public class SQLiteConditionInterpreter {

    public String explain(Condition condition) {


        StringBuilder builder = new StringBuilder();

        while (condition != null) {
            if(builder.length()>0) {
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



        if(value instanceof Date) return String.valueOf(((Date) value).getTime());

        if (ClassUtils.wrapperToPrimitive(value.getClass()) == null) {
            //判断数组
            //return value.toString();
        }

        return "'" + String.valueOf(value) + "'";

    }

}
