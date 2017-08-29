package com.charles.spider.scheduler.module;

import com.charles.spider.fetch.Extractor;
import com.charles.spider.scheduler.persist.Service;
import com.charles.spider.transfer.entity.ModuleType;
import com.charles.spider.transfer.entity.Module;
import com.charles.spider.scheduler.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-3-16.
 */
public class ComponentCoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(ComponentCoreFactory.class);

    private Service<Module> service;

    private GlobalComponentProxy globalProxy;
    private CommonComponentProxy commonProxy;
    private ExtractorComponentProxy extractorProxy;


    public ComponentCoreFactory(Service<Module> service) throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        this.service = service;

        commonProxy = new CommonComponentProxy(service, Paths.get(Config.INIT_DATA_PATH, "common"));

        extractorProxy = new ExtractorComponentProxy(commonProxy, service, Paths.get(Config.INIT_DATA_PATH, "extractor"));

        globalProxy = new GlobalComponentProxy(this, service);
    }


    public ComponentProxy proxy(ModuleType type) {
        switch (type) {
            case COMMON:
                return commonProxy;
            case EXTRACTOR:
                return extractorProxy;
            default:
                return null;
        }
    }


    public ComponentProxy proxy() {
        return globalProxy;
    }


    public Extractor extractorComponent(String componentName) throws IOException, ComponentBuildException {

        return extractorProxy.component(componentName);

    }
}
