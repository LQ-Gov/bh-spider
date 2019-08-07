package com.bh.spider.client;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;


public class ClientCrawlerTest {
    Client client = null;

    @Before
    public void before() throws IOException, URISyntaxException {
        client = new Client("127.0.0.1:8033");
        client.connect();
    }


    @Test
    public void crawler0() throws MalformedURLException, ExecutionException, InterruptedException {
        client.crawler("http://www.baidu.com/",CrawlerExtractor.class).get();
    }
}
