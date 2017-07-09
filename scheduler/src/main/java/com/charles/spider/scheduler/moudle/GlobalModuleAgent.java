package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.moudle.ModuleType;
import com.charles.spider.query.Query;
import com.charles.spider.store.entity.Module;
import com.charles.spider.store.service.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by lq on 7/7/17.
 */
public class GlobalModuleAgent extends ModuleAgent {
    public GlobalModuleAgent(ModuleType type, String basePath, Service<Module> service) throws IOException {
        super(type, basePath, service);
    }

    @Override
    public ModuleType type() {
        return null;
    }

    @Override
    public List<Module> select(int skip, int size) {
        Query query = new Query();
        query.skip(skip).limit(size);

        return service().select(query);
    }
}
