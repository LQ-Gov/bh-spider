package com.charles.spider.common.constant;

/**
 * Created by lq on 17-4-8.
 */
public enum ModuleTypes {
    UNKNOWN,
    JAR,
    GROOVY,
    CONFIG;


    public static ModuleTypes value(String s) {
        switch (s) {
            case "jar":
                return JAR;
            case "groovy":
                return GROOVY;

            default:
                return UNKNOWN;
        }
    }
}
