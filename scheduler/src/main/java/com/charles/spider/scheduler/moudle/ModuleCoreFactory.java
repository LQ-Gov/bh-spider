package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.constant.ModuleType;
import com.charles.spider.common.entity.Module;
import com.charles.spider.scheduler.config.Config;
import com.charles.spider.store.base.Store;
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
    private Store store;


    public ModuleCoreFactory(Store store) throws IOException {

        this.store = store;

        agents.put(ModuleType.GROOVY, new GroovyModuleAgent(Paths.get(Config.INIT_DATA_PATH, "module"), store));
        agents.put(ModuleType.CONFIG, new ModuleAgent(ModuleType.CONFIG, Paths.get(Config.INIT_DATA_PATH, "config"), store));
        agents.put(ModuleType.UNKNOWN, new GlobalModuleAgent(this, store));

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
