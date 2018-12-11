package com.bh.spider.scheduler.component;

import com.bh.spider.transfer.entity.Component;

import java.io.IOException;
import java.nio.file.Path;

public class JarComponentRepository extends ComponentRepository {
    private SpiderClassLoader loader = null;
    private Path dir;
    public JarComponentRepository(Path path) throws IOException {
        super(Component.Type.JAR,path);
        this.dir = path;

    }
    public ClassLoader classLoader() {
        return loader;
    }

}
