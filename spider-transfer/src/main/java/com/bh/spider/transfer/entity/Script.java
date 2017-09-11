package com.bh.spider.transfer.entity;


public class Script {
    private Operator operator;
    private Object[] args;

    private Script() {
    }

    private Script(Operator operator, Object[] args) {
        this.operator = operator;
        this.args = args;
    }

    public static Script wait(String cssSelector, long second) {
        return new Script(Operator.WAIT_UNTIL, new Object[]{cssSelector, second});
    }

    public static Script waitting(long second){
        return new Script(Operator.WAIT,new Object[]{second});
    }

    public static Script canClick(String cssSelector, long second) {
        return new Script(Operator.CAN_CLICK, new Object[]{cssSelector, second});
    }

    public static Script custom(String js) {
        return new Script(Operator.CUSTOM, new Object[]{js});
    }


    public Operator operator() {
        return operator;
    }

    public Object[] args() {
        return args;
    }

    public static enum Operator {
        WAIT, CUSTOM, CAN_CLICK,WAIT_UNTIL
    }
}
