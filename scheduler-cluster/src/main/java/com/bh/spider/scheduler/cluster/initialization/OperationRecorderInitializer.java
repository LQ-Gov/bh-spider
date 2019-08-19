package com.bh.spider.scheduler.cluster.initialization;

import com.bh.spider.scheduler.initialization.Initializer;

import java.nio.file.Path;

public class OperationRecorderInitializer implements Initializer<Void> {
    private Path path;
    private String[] names;
    private int cacheSize;

    public OperationRecorderInitializer(Path path,int cacheSize, String... names){
        this.path = path;
        this.cacheSize = cacheSize;
        this.names = names;

    }

    @Override
    public Void exec() throws Exception {

//        for(String name:names) {
//            OperationRecorder recorder = new OperationRecorder(name, path, cacheSize);
//            OperationRecorderFactory.register(recorder);
//        }

        return null;
    }
}
