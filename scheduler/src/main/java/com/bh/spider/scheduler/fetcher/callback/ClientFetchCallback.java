package com.bh.spider.scheduler.fetcher.callback;

import com.bh.spider.common.fetch.FetchContext;
import com.bh.spider.common.fetch.impl.FetchResponse;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.fetcher.FetchCallback;

/**
 * @author liuqi19
 * @version ClientFetchCallback, 2019-07-16 23:38 liuqi19
 **/
public class ClientFetchCallback implements FetchCallback {
    Context context;

    public ClientFetchCallback(Context context){
        this.context = context;
    }

    @Override
    public void run(FetchContext fetchContext, FetchResponse response) {
        context.commandCompleted(fetchContext.response());
    }

    @Override
    public void exception(Throwable e) {

    }
}
