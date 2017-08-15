package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.constant.ModuleType;
import com.charles.spider.common.entity.Module;
import com.charles.spider.scheduler.config.Config;
import com.charles.spider.store.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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

    private Map<ModuleType, ModuleAgent> agents = new HashMap<>();


    public ModuleCoreFactory(Service<Module> service) throws IOException {

        agents.put(ModuleType.JAR, new ModuleAgent(ModuleType.JAR, Paths.get(Config.INIT_DATA_PATH, "handler"), service));
        agents.put(ModuleType.GROOVY, new GroovyModuleAgent(ModuleType.GROOVY, Config.INIT_DATA_PATH + "handler", service));
        agents.put(ModuleType.CONFIG, new ModuleAgent(ModuleType.CONFIG, Paths.get(Config.INIT_DATA_PATH, "config"), service));
        agents.put(ModuleType.UNKNOWN, new GlobalModuleAgent(ModuleType.UNKNOWN, service));

    }


    public ModuleAgent agent(ModuleType type) {
        return agents.get(type);
    }

    public ModuleAgent agent() {
        return agents.get(ModuleType.UNKNOWN);
    }


    public Object object(String moduleName, String className) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {


        ModuleAgent agent = agents.get(ModuleType.UNKNOWN);

        Module module = agent.get(moduleName);

        agent = agents.get(module.getType());


        Object o = agent.object(moduleName, className);


        return o;
    }
}
