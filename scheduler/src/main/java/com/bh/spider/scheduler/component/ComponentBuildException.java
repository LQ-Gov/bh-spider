package com.bh.spider.scheduler.component;

public class ComponentBuildException extends Exception {
    private String module;
    private String message;

    public ComponentBuildException(String module, String message){
        this.module = module;
        this.message = message;
    }


    @Override
    public String getMessage() {
        return module+" build error:"+message;
    }
}
