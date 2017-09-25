package com.bh.spider.query.condition;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by lq on 17-6-23.
 */
public class Condition {
    protected final static Object NOT_SET = new Object();

    private String key;

    private Object value = NOT_SET;

    private Operator operator = Operator.IS;

    private Condition next;

    Condition() {
    }

    Condition(String key) {
        this.key = key;
    }


    public static Condition where(String key) {
        Preconditions.checkArgument(!StringUtils.isBlank(key), "the parameter %s can't empty", key);
        return new Condition(key);
    }

    public Condition and(Condition condition) {

        this.next = new AndCondition(condition);
        return this.next;

    }

    public Condition or(Condition conds) {
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
        this.value = value;
        return this;
    }


    public Condition in(Object value) {
        Preconditions.checkArgument(NOT_SET.equals(this.value)
                , "Multiple 'is' values declared. You need to use 'and' with multiple criteria");
        this.operator = Operator.IN;
        this.value = value;

        return this;
    }


    public Condition not(Object value) {
        Preconditions.checkArgument(NOT_SET.equals(this.value)
                , "Multiple 'is' values declared. You need to use 'and' with multiple criteria");
        operator = Operator.NOT;
        this.value = value;
        return this;
    }

    public Condition gt(Object value) {
        return this;
    }


    public Condition gte(Object value) {
        return this;
    }

    public Condition lt(Object value) {
        return this;
    }

    public Condition lte(Object value) {
        return this;
    }

    public boolean isValid() {
        return value != NOT_SET;
    }

    public Condition next() {
        return next;
    }

}
