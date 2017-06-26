package com.charles.spider.store.sqlite;

import com.charles.spider.store.condition.Condition;
import com.charles.spider.store.condition.Operator;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

/**
 * Created by lq on 17-6-23.
 */
public class SQLiteConditionInterpreter {

    public String explain(Condition condition) {
        String key = condition.key();
        Operator operator = condition.operator();

        switch (operator){
            case NOT: return key+"!="+ valueToString(condition.value());
            case IS: return key+"="+ valueToString(condition.value());
        }

        return "";
    }


    private String valueToString(Object value) {
        if(value==null) return "null";
        if (value instanceof String) return "'" + value + "'";

        if(ClassUtils.wrapperToPrimitive(value.getClass())==null){
            //判断数组
            return value.toString();
        }

        return String.valueOf(value);

    }

}
