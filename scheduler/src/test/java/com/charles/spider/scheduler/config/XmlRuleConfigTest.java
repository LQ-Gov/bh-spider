package com.charles.spider.scheduler.config;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by lq on 17-3-29.
 */
public class XmlRuleConfigTest {
    @Test
    public void parse() throws Exception {
        XmlRuleConfig cfg = new XmlRuleConfig("spider-rule.xml");
    }

}