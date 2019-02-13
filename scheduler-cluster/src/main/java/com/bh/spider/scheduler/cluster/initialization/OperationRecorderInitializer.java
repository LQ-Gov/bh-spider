package com.bh.spider.scheduler.cluster.initialization;

import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorder;
import com.bh.spider.scheduler.cluster.consistent.operation.OperationRecorderFactory;
import com.bh.spider.scheduler.initialization.Initializer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class OperationRecorderInitializer implements Initializer<Void> {
    private String base;
    private String[] names;
    private int cacheSize;

    public OperationRecorderInitializer(String basePath,int cacheSize, String... names){
        this.base = basePath;
        this.cacheSize = cacheSize;
        this.names = names;

    }

    @Override
    public Void exec() throws Exception {

        for(String name:names) {
            Path path = Paths.get(base,name);

            OperationRecorder recorder = new OperationRecorder(path, cacheSize);
            OperationRecorderFactory.register(recorder);
        }

        return null;
    }
}
