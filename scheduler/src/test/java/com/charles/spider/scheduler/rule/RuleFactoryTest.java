package com.charles.spider.scheduler.rule;

import com.charles.spider.common.rule.Rule;
import org.junit.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lq on 7/6/17.
 */
public class RuleFactoryTest {


    @Test
    public void save() throws Exception {


        String path = "src/test/resources/rule";

        RuleFactory factory = new RuleFactory(path);
        Rule rule = new Rule("save-test","www.sina.com","*******");
        rule.setPattern("pattern-demo");
        factory.save(rule);


    }

    @Test
    public void get() throws Exception {
        String path = RuleFactoryTest.class.getClassLoader().getResource("rule/www.baidu.com.json").getPath();
        RuleFactory factory = new RuleFactory(path);
        List<Rule> list = factory.get();

        String[] chains =list.get(0).extractor("200");
        System.out.println( Arrays.toString(chains));
    }

}