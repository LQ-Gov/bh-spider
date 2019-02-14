package com.bh.spider.scheduler.component;

import com.bh.spider.transfer.entity.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JarComponentRepository extends ComponentRepository {
    private JarComponentClassLoader classLoader;
    private Path dir;

    public JarComponentRepository(Path path) throws IOException {
        super(Component.Type.JAR, path);
        this.dir = path;


        this.classLoader = new JarComponentClassLoader(JarComponentRepository.class.getClassLoader());

        for (Component component : metadata().components())
            addComponentToClassloader(component);

    }
    public ClassLoader classLoader() {
        return classLoader;
    }

    @Override
    public Class<?> loadClass(String name) throws IOException, ClassNotFoundException {
        return classLoader.loadClass(name);
    }

    @Override
    public Component save(byte[] data, String name, String description, boolean override) throws Exception {
        Component component = super.save(data, name, description, override);
        addComponentToClassloader(component);
        return component;
    }


    private void addComponentToClassloader(Component component) throws IOException {
        Path path = Paths.get(dir.toString(),component.getName());
        this.classLoader.addJar(path);
    }

    private void deleteComponentToClassloader(Component component) throws IOException {
        Path path = Paths.get(dir.toString(),component.getName());
        this.classLoader.removeJar(path);
    }

    @Override
    public void delete(String name) throws IOException {

        Component component = metadata().get(name);
        super.delete(name);
        deleteComponentToClassloader(component);
    }
}
