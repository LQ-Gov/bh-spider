package com.charles.spider.store.base;

/**
 * Created by lq on 17-3-24.
 */
public class Field {
    public static final String ID = "id";
    public static final String URL = "url";
    public static final String CREATED_TIME = "createdTime";
    public static final String FINISH_TIME = "finishedTime";
    public static final String PROCESS = "process";
    public static final String STATUS = "status";
    public static final String LOG = "log";

    private String key;
    private String value;

    public Field(String key,Object value){

    }
}