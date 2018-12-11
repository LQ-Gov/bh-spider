package com.bh.spider.scheduler.component;

import com.bh.spider.fetch.Extractor;
import com.bh.spider.query.Query;
import com.bh.spider.store.service.Service;
import com.bh.spider.transfer.entity.Component;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroovyComponentRepository extends ComponentRepository {

    private JarComponentRepository commonProxy;
    private final static Map<String, Extractor> moduleObjects = new ConcurrentHashMap<>();

    private Path dir;

    public GroovyComponentRepository(JarComponentRepository commonProxy, Path path) throws IOException {
        super(Component.Type.GROOVY, path);
        this.commonProxy = commonProxy;
        this.dir = path;

    }

    @Override
    public void delete(String name) throws IOException {
        super.delete(name);


        moduleObjects.remove(name);


    }

    @Override
    public Component save(byte[] data, String name, String description, boolean override) throws Exception {
        Component component = super.save(data, name, description, override);

        Extractor extractor = moduleObjects.remove(name);

        if (extractor != null) {
            component(name);
        }

        return component;
    }

    public Extractor component(String name) throws IOException, ComponentBuildException {

        Extractor o = moduleObjects.get(name);

        if (o != null) return o;

        synchronized (moduleObjects) {
            o = moduleObjects.get(name);
            if (o != null) return o;

            GroovyClassLoader loader = new GroovyClassLoader(commonProxy.classLoader());

            Component component = super.get(name);
            if (component == null) throw new ComponentBuildException(name, "component not found");

            Class<?> cls = loader.parseClass(Paths.get(dir.toString(), name).toFile());

            if (cls == null) throw new ComponentBuildException(name, "can't find any class");

            try {
                o = (Extractor) cls.newInstance();
                moduleObjects.put(name, o);

            } catch (Exception e) {
                throw new ComponentBuildException(name, e.getMessage());
            }
        }

        return o;

    }


}
