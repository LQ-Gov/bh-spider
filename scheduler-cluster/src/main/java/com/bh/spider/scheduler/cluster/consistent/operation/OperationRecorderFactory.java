package com.bh.spider.scheduler.cluster.consistent.operation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OperationRecorderFactory {

    private final static Map<String,OperationRecorder> RECORDERS = new ConcurrentHashMap<>();



    public static void register(OperationRecorder recorder) {
        RECORDERS.put(recorder.name(), recorder);
    }


    public static OperationRecorder get(String name){
        return RECORDERS.get(name);
    }




}
