package com.charles.spider.scheduler.module;

import com.charles.spider.fetch.Extractor;
import com.charles.spider.query.Query;
import com.charles.spider.scheduler.persist.Service;
import com.charles.spider.transfer.entity.Module;
import com.charles.spider.transfer.entity.ModuleType;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtractorComponentProxy extends ComponentProxy {

    private CommonComponentProxy commonProxy;
    private Service<Module> service;
    private final static Map<String, Extractor> moduleObjects = new ConcurrentHashMap<>();

    public ExtractorComponentProxy(CommonComponentProxy commonProxy, Service<Module> service, Path path) {
        super(ModuleType.EXTRACTOR, service, path);
        this.commonProxy = commonProxy;
        this.service = service;
    }

    @Override
    public Module delete(Query query) throws IOException {
        Module module = super.delete(query);

        if (module != null) {

            moduleObjects.remove(module.getName());
        }

        return module;
    }

    @Override
    public Module save(byte[] data, String name, ModuleType type, String description, boolean override) throws Exception {
        Module module = super.save(data, name, type, description, override);

        Extractor extractor = moduleObjects.remove(name);

        if (extractor != null) {
            component(name);
        }

        return module;
    }

    public Extractor component(String moduleName) throws IOException, ComponentBuildException {

        Extractor o = moduleObjects.get(moduleName);

        if (o != null) return o;

        synchronized (moduleObjects) {
            o = moduleObjects.get(moduleName);
            if (o != null) return o;

            GroovyClassLoader loader = new GroovyClassLoader(commonProxy.classLoader());

            Module module = super.get(moduleName);
            if (module == null) throw new ComponentBuildException(moduleName, "module not found");

            Class<?> cls = loader.parseClass(new File(module.getPath() + "/data"));

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
