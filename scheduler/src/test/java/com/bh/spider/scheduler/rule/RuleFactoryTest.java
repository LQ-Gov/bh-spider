package com.bh.spider.scheduler.rule;

import com.bh.spider.rule.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lq on 7/6/17.
 */
public class RuleFactoryTest {
    private List<Boolean> o = Arrays.asList(true, false, true, false);


    @Test
    public void save() throws Exception {


        String path = "src/test/resources/rule";

        RuleFactory factory = new RuleFactory(path);
        Rule rule = new Rule("save-test", "www.sina.com", "*******");
        rule.setPattern("pattern-demo");
        factory.save(rule);


    }

    @Test
    public void get() throws Exception {
        String path = RuleFactoryTest.class.getClassLoader().getResource("rule/www.baidu.com.json").getPath();
        RuleFactory factory = new RuleFactory(path);
        List<Rule> list = factory.get();

        String[] chains = list.get(0).extractor("200");
        System.out.println(Arrays.toString(chains));
    }

    @Test
    public void t() throws NoSuchFieldException {

        Map<String, String> map = new ConcurrentHashMap<>();

        String s = map.computeIfAbsent("1", k -> "123");

//        System.out.println(map.putIfAbsent ("1", "100"));
//        System.out.println(map.putIfAbsent("1", "123"));
    }


}