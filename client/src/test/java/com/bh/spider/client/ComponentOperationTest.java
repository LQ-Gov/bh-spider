package com.bh.spider.client;

import com.bh.spider.common.component.Component;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

/**
 * Created by lq on 7/10/17.
 */
public class ComponentOperationTest {

    Client client = null;

    @Before
    public void before() throws IOException, URISyntaxException {
        Properties properties = new Properties();
        properties.setProperty("class.file.base.path",System.getProperty("user.dir")+"/src/test/java");
        client = new Client("127.0.0.1:8033",properties);
        client.open();
    }


    @Test
    public void select() throws Exception {

         List<Component> list = client.component().select(Component.Type.GROOVY);

         System.out.println(list.get(0).getName());
    }

    @Test
    public void select1() throws Exception {
        client.component().submit(TouTiaoExtractor.class);
    }


    @Test
    public void select2(){
        client.component().select(Component.Type.JAR);
    }


}