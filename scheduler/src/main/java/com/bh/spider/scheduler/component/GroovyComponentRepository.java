package com.bh.spider.scheduler.component;

import com.bh.spider.transfer.entity.Component;
import groovy.lang.GroovyClassLoader;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.*;

public class GroovyComponentRepository extends ComponentRepository {

    private final static Map<String, WeakReference<Proxy>> classCache = new ConcurrentHashMap<>();

    private Path dir;
    private ClassLoader parentClassLoader;

    public GroovyComponentRepository(ClassLoader parentClassLoader, Path path) throws IOException {
        super(Component.Type.GROOVY, path);
        this.dir = path;
        this.parentClassLoader = parentClassLoader;

    }

    @Override
    public void delete(String name) throws IOException {
        super.delete(name);


        clearCache(name);
    }

    @Override
    public Class<?> loadClass(String name) throws IOException {

        Component component = metadata().get(name);

        WeakReference<Proxy> reference = classCache.get(name);
        Proxy proxy;
        if (reference != null && (proxy = reference.get()) != null)
            return proxy.cls();


        Path path = Paths.get(dir.toString(), component.getName());

        GroovyComponentClassLoader classLoader = new GroovyComponentClassLoader(parentClassLoader);


        Class<?> cls = classLoader.parseClass(path.toFile());

        reference = new WeakReference<>(new Proxy(classLoader, cls, component));

        classCache.put(name, reference);


        return cls;
    }

    @Override
    public Component save(byte[] data, String name, String description, boolean override) throws Exception {
        Component component = super.save(data, name, description, override);

        clearCache(component.getName());

        return component;
    }


    private void clearCache(String name) {
        if (name == null) return;
        WeakReference<Proxy> reference = classCache.remove(name);

        Proxy proxy;

        if (reference != null && (proxy = reference.get()) != null)
            proxy.classLoader().clearCache();
    }


    private class Proxy {
        private GroovyClassLoader classLoader;
        private Class<?> cls;
        private Component component;

        public Proxy(GroovyClassLoader classLoader, Class<?> cls, Component component) {
            this.classLoader = classLoader;
            this.cls = cls;
            this.component = component;
        }


        public GroovyClassLoader classLoader() {
            return this.classLoader;
        }

        public Class<?> cls() {
            return cls;
        }

        public Component component() {
            return component;
        }
    }

}
