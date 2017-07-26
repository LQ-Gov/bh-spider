package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.constant.ModuleTypes;
import com.charles.spider.common.entity.Module;
import com.charles.spider.store.service.Service;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroovyModuleAgent extends ModuleAgent {

    private final static Map<String, Object> moduleObjects = new ConcurrentHashMap<>();

    public GroovyModuleAgent(ModuleTypes type, String basePath, Service service) throws IOException {
        super(type, basePath, service);
    }


    @Override
    public Object object(String moduleName, String className) throws IllegalAccessException, InstantiationException, IOException {

        String key = moduleName + "." + className;

        Object o = moduleObjects.get(key);

        if (o != null) return o;

        synchronized (moduleObjects) {
            o = moduleObjects.get(key);
            if (o != null) return o;


            Module module = super.get(moduleName);

            GroovyClassLoader loader = new GroovyClassLoader(ClassLoader.getSystemClassLoader());

            Class<?> cls = loader.parseClass(new File(module.getPath()));

            if (cls != null) {
                o = cls.newInstance();
                moduleObjects.put(key, o);
            }
        }

        return o;

    }
}
