package com.bh.spider.scheduler.component;

import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.store.service.Service;
import com.bh.spider.transfer.entity.Component;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CommonComponentProxy extends ComponentProxy {
    private SpiderClassLoader loader = null;

    public CommonComponentProxy(Service<Component> service, Path path) throws MalformedURLException, FileNotFoundException {
        super(Component.Type.COMMON, service, path);
        loader = new SpiderClassLoader(getClass().getClassLoader());

        Query query = Query.Condition(Condition.where("type").is(Component.Type.COMMON));

        List<Component> list = service.select(query);

        for(Component it:list) {

            this.loader.addJar(Paths.get(it.getPath(), "data"));
        }


    }

    public ClassLoader classLoader() {
        return loader;
    }

    @Override
    public Component save(byte[] data, String name, Component.Type type, String description, boolean override) throws Exception {
        Component component = super.save(data, name, type, description, override);

        this.loader.addJar(Paths.get(component.getPath(), "data"));

        return component;
    }
}
