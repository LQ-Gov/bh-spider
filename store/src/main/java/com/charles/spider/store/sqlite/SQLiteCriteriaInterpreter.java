package com.charles.spider.store.sqlite;

import com.charles.spider.store.condition.Condition;
import com.charles.spider.store.condition.Operator;

/**
 * Created by lq on 17-6-23.
 */
public class SQLiteCriteriaInterpreter {

    public String explain(Condition condition) {
        String key = condition.key();
        Operator operator = condition.operator();

        switch (operator){
            case NOT: return key+"!="+vauleToString(condition.value());
            case IS: return key+="="+vauleToString(condition.value());
        }

        return "";
    }


    private String vauleToString(Object value){
        return value.toString();
    }

}
