package com.bh.spider.scheduler.component;

import com.bh.common.utils.Json;
import com.bh.spider.scheduler.Config;
import com.bh.spider.common.component.Component;
import com.fasterxml.jackson.databind.type.MapType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by lq on 17-3-16.
 */
public class ComponentCoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(ComponentCoreFactory.class);

    private Map<Component.Type, ComponentRepository> componentRepositories = new HashMap<>();

    public ComponentCoreFactory(Config cfg) throws IOException {
        String dataPath = cfg.get(Config.INIT_COMPONENT_PATH);

        JarComponentRepository jarComponentRepository = new JarComponentRepository(Paths.get(dataPath, Component.Type.JAR.name()));

        componentRepositories.put(Component.Type.JAR, jarComponentRepository);
        componentRepositories.put(Component.Type.GROOVY,
                new GroovyComponentRepository(jarComponentRepository.classLoader(), Paths.get(dataPath, Component.Type.GROOVY.name())));


    }


    public ComponentRepository proxy(Component.Type type) {
        return componentRepositories.get(type);
    }


    public ComponentRepository proxy(String name) {
        Collection<ComponentRepository> repositories = componentRepositories.values();
        for (ComponentRepository repository : repositories) {
            if (repository.get(name) != null)
                return repository;
        }

        return null;
    }


    public List<Component> all() {
        Collection<ComponentRepository> repositories = componentRepositories.values();
        List<Component> components = new LinkedList<>();

        for (ComponentRepository repository : repositories) {
            components.addAll(repository.all());
        }

        return components;
    }

    public byte[] snapshot() {
        Collection<ComponentRepository> repositories = componentRepositories.values();
        Map<Component.Type, List<Component>> map = new HashMap<>();
        for (ComponentRepository repository : repositories) {
            map.put(repository.componentType(), repository.all());
        }

        try {
            return Json.get().writeValueAsBytes(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void apply(byte[] snap) {
        MapType type = Json.mapType(Json.constructType(Component.Type.class), Json.constructCollectionType(List.class, Component.class));
        try {
            Map<Component.Type, List<Component>> map = Json.get().readValue(snap, type);

            map.forEach(this::reset);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void reset(Component.Type type, List<Component> components) {
        componentRepositories.get(type);
    }


}
