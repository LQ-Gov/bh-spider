package com.charles.spider.scheduler.moudle;

import com.charles.spider.scheduler.persist.Service;
import com.charles.spider.transfer.entity.ModuleType;
import com.charles.spider.transfer.entity.Module;
import com.charles.spider.scheduler.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-3-16.
 */
public class ModuleCoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(ModuleCoreFactory.class);

    private Map<ModuleType, ModuleAgent> agents = new HashMap<>();
    private Service<Module> service;


    public ModuleCoreFactory(Service<Module> service) throws IOException {

        this.service = service;

        agents.put(ModuleType.GROOVY, new GroovyModuleAgent(Paths.get(Config.INIT_DATA_PATH, "module"), service));
        agents.put(ModuleType.CONFIG, new ModuleAgent(ModuleType.CONFIG, Paths.get(Config.INIT_DATA_PATH, "config"), service));
        agents.put(ModuleType.UNKNOWN, new GlobalModuleAgent(this, service));

    }


    public ModuleAgent agent(ModuleType type) {
        return agents.get(type);
    }

    public ModuleAgent agent() {
        return agents.get(ModuleType.UNKNOWN);
    }


    public Object object(String moduleName, String className) throws IOException, ModuleBuildException {


        Module module = agent().get(moduleName);

        ModuleAgent agent = agents.get(module.getType());


        Object o = agent.object(moduleName, className);


        return o;
    }
}
