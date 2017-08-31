package com.bh.spider.client;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.transfer.CommandCode;

import java.net.MalformedURLException;

/**
 * Created by lq on 7/9/17.
 */
public class RequestOperation {

    private Client client =null;

    RequestOperation(Client client){
        this.client = client;
    }




    public void submit(Request req){
        client.write(CommandCode.SUBMIT_REQUEST,null,req);
    }


    public void submit(String url) throws MalformedURLException {
        Request req = new FetchRequest(url);
        submit(req);
    }
}
