package com.bh.spider.scheduler.module;

import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.scheduler.persist.Service;
import com.bh.spider.transfer.entity.Component;
import com.bh.spider.transfer.entity.ModuleType;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CommonComponentProxy extends ComponentProxy {
    private SpiderClassLoader loader = null;

    public CommonComponentProxy(Service<Component> service, Path path) throws MalformedURLException, FileNotFoundException {
        super(ModuleType.COMMON, service, path);
        loader = new SpiderClassLoader(getClass().getClassLoader());

        Query query = Query.Condition(Condition.where("type").is(ModuleType.COMMON));

        List<Component> list = service.select(query);

        for(Component it:list) {

            this.loader.addJar(Paths.get(it.getPath(), "data"));
        }


    }

    public ClassLoader classLoader() {
        return loader;
    }

    @Override
    public Component save(byte[] data, String name, ModuleType type, String description, boolean override) throws Exception {
        Component component = super.save(data, name, type, description, override);

        this.loader.addJar(Paths.get(component.getPath(), "data"));

        return component;
    }
}
