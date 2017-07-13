package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.constant.ModuleTypes;
import com.charles.spider.scheduler.config.Config;
import com.charles.spider.store.base.Store;
import com.charles.spider.common.entity.Module;
import com.charles.spider.store.service.Service;
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


    private Map<ModuleTypes, ModuleAgent> agents = new HashMap<>();
    private static volatile ModuleCoreFactory obj = null;

    public ModuleCoreFactory(Service<Module> service) throws IOException {

        agents.put(ModuleTypes.JAR, new ModuleAgent(ModuleTypes.JAR, Paths.get(Config.INIT_DATA_PATH, "handler"), service));
        agents.put(ModuleTypes.CONFIG, new ModuleAgent(ModuleTypes.CONFIG, Paths.get(Config.INIT_DATA_PATH, "config"), service));
        agents.put(ModuleTypes.UNKNOWN, new GlobalModuleAgent(ModuleTypes.UNKNOWN, service));
    }


    public ModuleAgent agent(ModuleTypes type) {
        return agents.get(type);
    }

    public ModuleAgent agent() {
        return agents.get(ModuleTypes.UNKNOWN);
    }


    public static ModuleCoreFactory instance() throws Exception {
        if (obj == null) {
            synchronized (ModuleCoreFactory.class) {
                if (obj == null) {

                    obj = new ModuleCoreFactory(Store.get(Config.INIT_STORE_DATABASE, Config.getStoreProperties()).module());
                }
            }
        }
        return obj;
    }
}
