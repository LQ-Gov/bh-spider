package com.charles.spider.common.constant;

/**
 * Created by lq on 17-4-8.
 */
public enum ModuleTypes {
    UNKNOWN,
    JAR,
    CONFIG;


    public ModuleTypes value(String s){
        switch (s) {
            case "jar":
                return JAR;

            default:
                return UNKNOWN;
        }
    }
}
