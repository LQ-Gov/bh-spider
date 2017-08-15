package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.constant.ModuleType;
import com.charles.spider.common.entity.Module;
import com.charles.spider.query.Query;
import com.charles.spider.store.service.Service;
import com.google.common.base.Preconditions;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroovyModuleAgent extends ModuleAgent {

    private final static Map<String, Object> moduleObjects = new ConcurrentHashMap<>();

    public GroovyModuleAgent(ModuleType type, String basePath, Service service) throws IOException {
        super(type, basePath, service);
    }


    @Override
    public void delete(Query query) {
        List<Module> list = this.select(query);

        Preconditions.checkState(list != null && !list.isEmpty(), "module is not exist");

        Module module = list.get(0);

        String key = module.getName() + ".null";

        moduleObjects.remove(key);

        super.delete(query);
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

            Class<?> cls = loader.parseClass(new File(module.getPath() + "/data"));


            if (cls != null) {
                o = cls.newInstance();
                moduleObjects.put(key, o);
            }
        }

        return o;

    }
}
