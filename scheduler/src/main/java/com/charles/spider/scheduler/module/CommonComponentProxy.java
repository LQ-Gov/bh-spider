package com.charles.spider.scheduler.module;

import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.scheduler.persist.Service;
import com.charles.spider.transfer.entity.Module;
import com.charles.spider.transfer.entity.ModuleType;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CommonComponentProxy extends ComponentProxy {
    private SpiderClassLoader loader = null;

    public CommonComponentProxy(Service<Module> service, Path path) throws MalformedURLException, FileNotFoundException {
        super(ModuleType.COMMON, service, path);
        loader = new SpiderClassLoader(getClass().getClassLoader());

        Query query = Query.Condition(Condition.where("type").is(ModuleType.COMMON));

        List<Module> list = service.select(query);

        for(Module it:list) {

            this.loader.addJar(Paths.get(it.getPath(), "data"));
        }


    }

    public ClassLoader classLoader() {
        return loader;
    }

    @Override
    public Module save(byte[] data, String name, ModuleType type, String description, boolean override) throws Exception {
        Module module = super.save(data, name, type, description, override);

        this.loader.addJar(Paths.get(module.getPath(), "data"));

        return module;
    }
}
