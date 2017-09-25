package com.bh.spider.client;

import com.bh.spider.transfer.entity.Component;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by lq on 7/10/17.
 */
public class ComponentOperationTest {

    Client client = null;

    @Before
    public void before() throws IOException, URISyntaxException {
        client = new Client("127.0.0.1:8033");
    }


    @Test
    public void select() throws Exception {

         List<Component> list = client.component().select();

         System.out.println(list.get(0).getName());
    }

    @Test
    public void select1() throws Exception {
    }


}