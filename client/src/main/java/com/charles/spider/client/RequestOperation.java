package com.charles.spider.client;

import com.charles.spider.common.command.Commands;
import com.charles.spider.common.http.Request;

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
        client.write(Commands.SUBMIT_REQUEST,null,req);
    }


    public void submit(String url) throws MalformedURLException {
        Request req = new Request(url);
        submit(req);
    }
}
