package com.bh.spider.scheduler.rule;

public class RuleBindException extends Exception {

    private String message=null;

    public RuleBindException(String  message){
        this.message = message;
    }


    @Override
    public String getMessage() {
        return message;
    }
}
