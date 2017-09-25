package com.bh.spider.client;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by lq on 17-3-26.
 */
public class ClientTest {

    private Client client = null;

    @Before
    public void before() throws IOException, URISyntaxException {
        client = new Client("127.0.0.1:8033");
    }


    @Test
    public void submit() throws Exception {
        client.component().submit("target/client-1.0-SNAPSHOT.jar");
    }

}