package com.bh.spider.scheduler.component;

import com.bh.spider.fetch.Extractor;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.transfer.entity.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-3-16.
 */
public class ComponentCoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(ComponentCoreFactory.class);

    private Map<Component.Type,ComponentRepository> componentRepositories = new HashMap<>();

    public ComponentCoreFactory(Config cfg) throws IOException {
        String dataPath = cfg.get(Config.INIT_DATA_PATH);

        componentRepositories.put(Component.Type.JAR, new JarComponentRepository(Paths.get(dataPath, Component.Type.JAR.name())));
        componentRepositories.put(Component.Type.GROOVY,
                new GroovyComponentRepository(null, Paths.get(dataPath, Component.Type.GROOVY.name())));


    }


    public ComponentRepository proxy(Component.Type type) {
        return componentRepositories.get(type);
    }
}
