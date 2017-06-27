package com.charles.spider.store.condition;

import com.charles.spider.store.base.Criteria;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import sun.invoke.util.VerifyAccess;

import java.util.List;

/**
 * Created by lq on 17-6-23.
 */
public class Condition {
    protected final static Object NOT_SET = new Object();

    private String key;

    private Object value = NOT_SET;

    private Operator operator = Operator.IS;

    private List<Condition> conditions;

    private Condition next;

    Condition(){}

    Condition(String key) {
        this.key = key;
    }


    public static Condition where(String key) {
        Preconditions.checkArgument(!StringUtils.isBlank(key),"the parameter %s can't empty",key);
        return new Condition(key);
    }

    public Condition and(Condition condition){

        this.next = new AndCondition(condition);
        return this.next;

    }

    public Condition or(Condition conds){
        return null;
    }


    public String key() {
        return key;
    }

    public Operator operator() {
        return operator;
    }

    public Object value() {
        return value;
    }


    public Condition is(Object value) {

        Preconditions.checkArgument(this.value.equals(NOT_SET)
                , "Multiple 'is' values declared. You need to use 'and' with multiple criteria");

        Preconditions.checkArgument(!lastOperatorWasNot(), "Invalid query: 'not' can't be used with 'is' - use 'ne' instead.");

        this.value = value;
        return this;
    }

    private boolean lastOperatorWasNot() {
        return this.conditions != null
                && !this.conditions.isEmpty()
                && conditions.get(conditions.size() - 1).operator == Operator.NOT;
    }


    public Condition not(Object value) {
        Preconditions.checkArgument(NOT_SET.equals(this.value)
                , "Multiple 'is' values declared. You need to use 'and' with multiple criteria");
        operator = Operator.NOT;
        this.value = value;
        return this;
    }

    public Condition next(){return next;}

}
