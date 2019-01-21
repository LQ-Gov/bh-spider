package com.bh.spider.scheduler;

import com.bh.spider.scheduler.domain.pattern.AntPatternMatcher;
import com.bh.spider.scheduler.domain.pattern.AntRulePattern;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class RuleMatcherTest {
    AntPatternMatcher matcher = new AntPatternMatcher();


    @Test
    public void test0() throws MalformedURLException {

        AntRulePattern pattern = new AntRulePattern("http://www.zhihu.com/*/se?rch\\?id={id}&type={type}");

        boolean res = pattern.match(new URL("http://www.zhihu.com/content/search?id=10112&type="));

        System.out.println(res);

    }
}
