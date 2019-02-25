package com.bh.spider.client;

import com.bh.spider.common.rule.Rule;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by lq on 7/17/17.
 */
public class RuleOperationTest {


    Client client = null;

    @Before
    public void before() throws IOException, URISyntaxException {
        client = new Client("127.0.0.1:8033");
        client.open();
    }

    @Test
    public void hosts() throws Exception {
        List<String> hosts = client.rule().hosts();

        System.out.println(hosts);
    }

    @Test
    public void select() throws Exception {

        List<Rule> rules = client.rule().select();

        System.out.println(rules);
    }


    @Test
    public void submit0(){

    }

}