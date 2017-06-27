package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.moudle.Description;
import com.charles.spider.common.moudle.ModuleType;
import com.charles.spider.scheduler.config.Config;
import com.charles.spider.store.base.Store;
import com.charles.spider.store.entity.Module;
import com.charles.spider.store.service.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.DigestException;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-3-16.
 */
public class ModuleCoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(ModuleCoreFactory.class);

    private Map<ModuleType, ModuleAgent> agents = new HashMap<>();
    private static volatile ModuleCoreFactory obj = null;

    public ModuleCoreFactory(Service<Module> service) throws IOException {
        agents.put(ModuleType.JAR, new ModuleAgent(Paths.get(Config.INIT_DATA_PATH, "handler"), service));
        agents.put(ModuleType.CONFIG, new ModuleAgent(Paths.get(Config.INIT_DATA_PATH, "config"), service));
    }


    public ModuleAgent agent(ModuleType type) {
        return agents.get(type);
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
