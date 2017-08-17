package com.charles.spider.scheduler.moudle;

public class ModuleBuildException extends Exception {
    private String module;
    private String message;

    public ModuleBuildException(String module, String message){
        this.module = module;
        this.message = message;
    }


    @Override
    public String getMessage() {
        return module+" build error:"+message;
    }
}
