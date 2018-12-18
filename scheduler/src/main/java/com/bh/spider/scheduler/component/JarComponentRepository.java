package com.bh.spider.scheduler.component;

import com.bh.spider.transfer.entity.Component;

import java.io.IOException;
import java.nio.file.Path;

public class JarComponentRepository extends ComponentRepository {
    private SpiderClassLoader loader = null;
    private JarComponentClassLoader classLoader;
    private Path dir;

    public JarComponentRepository(Path path) throws IOException {
        super(Component.Type.JAR, path);
        this.dir = path;

        this.classLoader = new JarComponentClassLoader(path, JarComponentRepository.class.getClassLoader());

    }
    public ClassLoader classLoader() {
        return loader;
    }

    @Override
    public Class<?> loadClass(String name) throws IOException, ClassNotFoundException {
        return classLoader.loadClass(name);
    }
}
