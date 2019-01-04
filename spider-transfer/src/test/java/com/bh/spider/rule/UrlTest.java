package com.bh.spider.rule;

import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlTest {


    @Test
    public void test() throws MalformedURLException {
        String url = "http://**.jd.**/";
        URL u  = new URL(url);

        System.out.println(u.getHost());
    }
}
