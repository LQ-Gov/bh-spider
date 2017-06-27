package com.charles.spider.store.condition;

/**
 * Created by lq on 17-6-27.
 */
public abstract class JoinCondition extends Condition {
    private Condition condition;

    public JoinCondition(Condition condition){
        this.condition = condition;
    }

    @Override
    public String key() {
        return condition.key();
    }

    @Override
    public Operator operator() {
        return condition.operator();
    }

    @Override
    public Object value() {
        return condition.value();
    }

}
