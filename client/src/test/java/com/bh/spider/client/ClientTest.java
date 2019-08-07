package com.bh.spider.client;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * Created by lq on 17-3-26.
 */
public class ClientTest {

    private Client client = null;

    @Before
    public void before() {
        client = new Client("127.0.0.1:8033");
        client.connect();
    }


    @Test
    public void submit() throws Exception {
        client.component().submit(Paths.get("target/client-1.0-SNAPSHOT.jar"));
    }

}