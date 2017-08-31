package com.bh.spider.client;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class RequestOperationTest {

    Client client = null;

    @Before
    public void before() throws IOException, URISyntaxException {
        client = new Client("127.0.0.1:8033");
    }

    @Test
    public void submit() throws Exception {
        client.request().submit("http://www.toutiao.com/a6444873109181874445");

    }
}