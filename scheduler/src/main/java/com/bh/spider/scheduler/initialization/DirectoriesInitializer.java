package com.bh.spider.scheduler.initialization;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoriesInitializer implements Initializer<Void> {
    private String base;
    private String[] dirs;
    public DirectoriesInitializer(String base,String... dirs) {
        this.base = base;
        this.dirs = dirs;
    }


    @Override
    public Void exec() throws IOException {
        for (String dir : dirs) {
            Files.createDirectories(Paths.get(base,dir));
        }
        return null;
    }
}
