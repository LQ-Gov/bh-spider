package com.bh.spider.scheduler.cluster.worker.store;

import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreAccessor;
import io.netty.channel.Channel;

import java.util.Properties;

public class RemoteStore implements Store {
    private Channel channel;
    private StoreAccessor accessor;
    public RemoteStore(Channel channel) {
        this.channel = channel;
        this.accessor = new RemoteStoreAccessor(channel);
    }


    @Override
    public String name() {
        return "Remote Store";
    }

    @Override
    public void connect() throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public Properties config() {
        return null;
    }

    @Override
    public StoreAccessor accessor() {
        return accessor;
    }
}
