package com.bh.spider.scheduler.cluster.worker.store;

import com.bh.spider.store.base.Store;
import com.bh.spider.store.base.StoreBuilder;
import io.netty.channel.Channel;

import java.util.Properties;

public class RemoteStoreBuilder implements StoreBuilder {


    public Channel channel;

    public RemoteStoreBuilder(Channel channel){
        this.channel = channel;
    }


    @Override
    public Store build(Properties properties) throws Exception {

        return new RemoteStore(channel);



    }
}
