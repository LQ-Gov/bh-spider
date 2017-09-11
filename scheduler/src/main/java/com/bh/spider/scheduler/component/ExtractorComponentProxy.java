package com.bh.spider.scheduler.component;

import com.bh.spider.fetch.Extractor;
import com.bh.spider.query.Query;
import com.bh.spider.scheduler.persist.Service;
import com.bh.spider.transfer.entity.Component;
import com.bh.spider.transfer.entity.ModuleType;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtractorComponentProxy extends ComponentProxy {

    private CommonComponentProxy commonProxy;
    private Service<Component> service;
    private final static Map<String, Extractor> moduleObjects = new ConcurrentHashMap<>();

    public ExtractorComponentProxy(CommonComponentProxy commonProxy, Service<Component> service, Path path) {
        super(ModuleType.EXTRACTOR, service, path);
        this.commonProxy = commonProxy;
        this.service = service;
    }

    @Override
    public Component delete(Query query) throws IOException {
        Component component = super.delete(query);

        if (component != null) {

            moduleObjects.remove(component.getName());
        }

        return component;
    }

    @Override
    public Component save(byte[] data, String name, ModuleType type, String description, boolean override) throws Exception {
        Component component = super.save(data, name, type, description, override);

        Extractor extractor = moduleObjects.remove(name);

        if (extractor != null) {
            component(name);
        }

        return component;
    }

    public Extractor component(String moduleName) throws IOException, ComponentBuildException {

        Extractor o = moduleObjects.get(moduleName);

        if (o != null) return o;

        synchronized (moduleObjects) {
            o = moduleObjects.get(moduleName);
            if (o != null) return o;

            GroovyClassLoader loader = new GroovyClassLoader(commonProxy.classLoader());

            Component component = super.get(moduleName);
            if (component == null) throw new ComponentBuildException(moduleName, "component not found");

            Class<?> cls = loader.parseClass(new File(component.getPath() + "/data"));

            if (cls == null) throw new ComponentBuildException(moduleName, "can't find any class");

            try {
                o = (Extractor) cls.newInstance();
                moduleObjects.put(moduleName, o);

            } catch (Exception e) {
                throw new ComponentBuildException(moduleName, e.getMessage());
            }
        }

        return o;

    }


}
