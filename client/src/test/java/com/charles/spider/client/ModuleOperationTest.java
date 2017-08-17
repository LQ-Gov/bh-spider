package com.charles.spider.client;

import com.charles.spider.transfer.entity.Module;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by lq on 7/10/17.
 */
public class ModuleOperationTest {

    Client client = null;

    @Before
    public void before() throws IOException, URISyntaxException {
        client = new Client("127.0.0.1:8033");
    }


    @Test
    public void select() throws Exception {

         List<Module> list = client.module().select();

         System.out.println(list.get(0).getName());
    }

    @Test
    public void select1() throws Exception {
    }


}