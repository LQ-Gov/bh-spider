package com.bh.spider.client;

import com.bh.spider.client.sender.Sender;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.impl.RequestBuilder;
import com.bh.common.utils.CommandCode;

import java.net.MalformedURLException;

/**
 * Created by lq on 7/9/17.
 */
public class RequestOperation {

    private Sender sender = null;

    RequestOperation(Sender sender) {
        this.sender = sender;
    }


    public void submit(Request req) {
        sender.write(CommandCode.SUBMIT_REQUEST, null, req);
    }


    public void submit(String url) throws MalformedURLException {
        Request req = RequestBuilder.create(url).build();
        submit(req);
    }

//    public List<Request> select(Query query) {
//        ParameterizedType type = ParameterizedTypeImpl.make(List.class, new Type[]{RequestImpl.class}, null);
//        return sender.write(CommandCode.GET_REQUEST_LIST, type, query);
//    }
}
