package com.bh.spider.scheduler.persist.sqlite;

import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.scheduler.persist.Service;
import com.bh.spider.scheduler.persist.Store;
import com.bh.spider.transfer.entity.Component;

public class SQLiteStore implements Store {
    public Service<Component> moduleService;
    private Service<FetchRequest> requestService;

    public SQLiteStore(){}

    @Override
    public Service<Component> module() {
        return moduleService;
    }

    @Override
    public Service<FetchRequest> request() {
        return requestService;
    }


    public void setModuleService(Service<Component> service){
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
