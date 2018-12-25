package com.bh.spider.scheduler.cluster.worker.store;

import com.bh.spider.fetch.Request;
import com.bh.spider.store.base.StoreAccessor;
import io.netty.channel.Channel;

import java.util.List;

public class RemoteStoreAccessor implements StoreAccessor {
    private Channel channel;
    public RemoteStoreAccessor(Channel channel){
        this.channel = channel;
    }

    @Override
    public boolean insert(Request request, long ruleId) {
        return false;
    }

    @Override
    public void update(long ruleId, Long[] reIds, Request.State state) {

    }

    @Override
    public void update(long id, Integer code, Request.State state, String message) {

    }

    @Override
    public List<Request> find(long ruleId, Request.State state, long size) {
        return null;
    }

    @Override
    public List<Request> find(long ruleId, Request.State state, long offset, long size) {
        return null;
    }

    @Override
    public long count(long ruleId, Request.State state) {
        return 0;
    }
}
