package com.bh.spider.scheduler.initialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoriesInitializer implements Initializer<Void> {
    private final static Logger logger = LoggerFactory.getLogger(DirectoriesInitializer.class);
    private String[] dirs;
    public DirectoriesInitializer(String... dirs) {
        this.dirs = dirs;
    }


    @Override
    public Void exec() throws IOException {
        for (String dir : dirs) {
            Path path = Paths.get(dir);
            Files.createDirectories(path);
            logger.info("create directory:{}",path);
        }
        return null;
    }
}
