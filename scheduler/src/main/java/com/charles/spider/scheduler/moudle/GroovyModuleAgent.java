package com.charles.spider.scheduler.moudle;

import com.charles.spider.transfer.entity.ModuleType;
import com.charles.spider.transfer.entity.Module;
import com.charles.spider.query.Query;
import com.charles.spider.store.base.Store;
import com.google.common.base.Preconditions;
import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GroovyModuleAgent extends ModuleAgent {

    private final static Map<String, Object> moduleObjects = new ConcurrentHashMap<>();

    public GroovyModuleAgent(Path basePath, Store store) {
        super(ModuleType.GROOVY, basePath, store);
    }


    @Override
    public void delete(Query query) throws IOException {
        Module module = this.store().single(Module.class, query);

        Preconditions.checkState(module != null, "module is not exist");

        String key = module.getName() + ".null";

        synchronized (moduleObjects) {
            moduleObjects.remove(key);

            super.delete(query);
        }
    }

    @Override
    public Object object(String moduleName, String className) throws IOException, ModuleBuildException {

        String key = moduleName + "." + className;

        Object o = moduleObjects.get(key);

        if (o != null) return o;

        synchronized (moduleObjects) {
            o = moduleObjects.get(key);
            if (o != null) return o;


            Module module = super.get(moduleName);
            if (module == null) throw new ModuleBuildException(moduleName, "module not found");

            GroovyClassLoader loader = new GroovyClassLoader(ClassLoader.getSystemClassLoader());

            Class<?> cls = loader.parseClass(new File(module.getPath() + "/data"));

            if (cls == null) throw new ModuleBuildException(moduleName, "can't find any class");


            try {
                o = cls.newInstance();
                moduleObjects.put(key, o);

            } catch (Exception e) {
                throw new ModuleBuildException(moduleName, e.getMessage());
            }
        }

        return o;

    }
}
