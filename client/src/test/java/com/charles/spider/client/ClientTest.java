package com.charles.spider.client;

import com.charles.spider.common.moudle.Description;
import com.charles.spider.common.moudle.ModuleType;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * Created by lq on 17-3-26.
 */
public class ClientTest {

    private Client client = null;

    @Before
    public void init() throws IOException, URISyntaxException {
        //client = new Client("127.0.0.1:8033");
        client = new Client();

    }


    @Test
    public void submit() throws Exception {
        Description desc = new Description(ModuleType.HANDLE);
        client.submit("target/client-1.0-SNAPSHOT.jar",desc,true);


    }

}