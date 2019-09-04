package com.bh.spider.client;

import com.bh.common.utils.CommandCode;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.impl.RequestBuilder;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * Created by lq on 7/9/17.
 */
public class RequestOperation {

    private Communicator communicator = null;

    RequestOperation(Communicator communicator) {
        this.communicator = communicator;
    }


    public void submit(Request req) {
        communicator.write(CommandCode.SUBMIT_REQUEST, null, req);
    }


    public void submit(String url) throws MalformedURLException {
        Request req = RequestBuilder.create(url).build();
        submit(req);
    }

    public Map<Request.State, Long> distributeCount() {
        ParameterizedType type = ParameterizedTypeImpl.make(Map.class, new Type[]{Request.State.class, Long.class}, null);
        return communicator.write(CommandCode.UNWATCH, type);
    }


    public long count(Long ruleId, Request.State state) {
        return communicator.write(CommandCode.URL_COUNT, Long.class, ruleId, state);
    }
}
