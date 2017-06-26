package com.charles.spider.common.moudle;

/**
 * Created by lq on 17-4-8.
 */
public enum ModuleType {
    UNKNOWN,
    JAR,
    CONFIG;


    public ModuleType value(String s){
        switch (s) {
            case "jar":
                return JAR;

            default:
                return UNKNOWN;
        }
    }
}
