package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.constant.ModuleTypes;
import com.charles.spider.common.entity.Module;
import com.charles.spider.scheduler.config.Config;
import com.charles.spider.store.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lq on 17-3-16.
 */
public class ModuleCoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(ModuleCoreFactory.class);

    private static Map<String, Object> moduleObjects = new ConcurrentHashMap<>();

    private Map<ModuleTypes, ModuleAgent> agents = new HashMap<>();


    public ModuleCoreFactory(Service<Module> service) throws IOException {

        agents.put(ModuleTypes.JAR, new ModuleAgent(ModuleTypes.JAR, Paths.get(Config.INIT_DATA_PATH, "handler"), service));
        agents.put(ModuleTypes.GROOVY, new GroovyModuleAgent(ModuleTypes.GROOVY, Config.INIT_DATA_PATH + "handler", service));
        agents.put(ModuleTypes.CONFIG, new ModuleAgent(ModuleTypes.CONFIG, Paths.get(Config.INIT_DATA_PATH, "config"), service));
        agents.put(ModuleTypes.UNKNOWN, new GlobalModuleAgent(ModuleTypes.UNKNOWN, service));

    }


    public ModuleAgent agent(ModuleTypes type) {
        return agents.get(type);
    }

    public ModuleAgent agent() {
        return agents.get(ModuleTypes.UNKNOWN);
    }


    public Object object(String moduleName, String className) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {


        ModuleAgent agent = agents.get(ModuleTypes.UNKNOWN);

        Module module = agent.get(moduleName);

        agent = agents.get(module.getType());


        Object o = agent.object(moduleName, className);

//        Object o = moduleObjects.get(key);
//
//        if (o == null) {
//
//
//            if (module == null) throw new FileNotFoundException("the module not exists");
//
//            synchronized (module.getType()) {
//                o = moduleObjects.get(key);
//                if (o != null) return o;
//
//                String path = module.getPath();
//
//                URLClassLoader cl = new URLClassLoader(new URL[]{new URL("file:///" + path)}, this.getClass().getClassLoader());
//
//                Class<?> cls = cl.loadClass(className);
//
//                o = cls.newInstance();
//
//                moduleObjects.put(key, o);
//            }
//        }


        return o;
    }
}
