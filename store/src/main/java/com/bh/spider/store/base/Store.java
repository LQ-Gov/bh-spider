package com.bh.spider.store.base;

import com.bh.spider.store.service.FetchService;

import java.util.Properties;

/**
 * Created by lq on 17-3-25.
 */
public interface Store {

    static StoreBuilder builder(String cls) throws Exception {
        StoreBuilder builder = (StoreBuilder) Class.forName(cls).newInstance();
        return builder;

    }

    /**
     * 打开连接
     * @throws Exception
     */
    void connect() throws Exception;

    /**
     * 关闭连接
     */
    void close() throws Exception;

    Properties config();


    StoreAccessor accessor();
}
