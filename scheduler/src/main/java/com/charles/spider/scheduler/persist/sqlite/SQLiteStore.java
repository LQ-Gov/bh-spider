package com.charles.spider.scheduler.persist.sqlite;

import com.charles.spider.fetch.impl.FetchRequest;
import com.charles.spider.scheduler.persist.Service;
import com.charles.spider.scheduler.persist.Store;
import com.charles.spider.transfer.entity.Module;

public class SQLiteStore implements Store {
    public Service<Module> moduleService;
    private Service<FetchRequest> requestService;

    public SQLiteStore(){}

    @Override
    public Service<Module> module() {
        return moduleService;
    }

    @Override
    public Service<FetchRequest> request() {
        return requestService;
    }


    public void setModuleService(Service<Module> service){
        this.moduleService = service;
    }

    public void setRequestService(Service<FetchRequest> service){
        this.requestService = service;
    }

    @Override
    public void init() {
        module().init();
        request().init();
    }
}
