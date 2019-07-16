package com.bh.spider.scheduler.fetcher;

import com.bh.spider.common.fetch.FetchContext;
import com.bh.spider.common.fetch.impl.FetchResponse;

/**
 * @author liuqi19
 * @version FetchCallback, 2019-07-16 18:13 liuqi19
 **/
public interface FetchCallback {



    void run(FetchContext fetchContext, FetchResponse response);


    void exception(Throwable e);
}
