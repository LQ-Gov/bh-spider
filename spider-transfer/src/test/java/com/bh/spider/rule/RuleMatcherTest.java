package com.bh.spider.rule;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RuleMatcherTest {

    @Test
    void match() throws Exception {

        RuleMatcher matcher = new RuleMatcher();


        Assertions.assertTrue(matcher.match("http://www.host.com/**","http://www.host.com"));
        Assertions.assertTrue(matcher.match("http://www.host.com/**","http://www.host.com/"));
        Assertions.assertTrue(matcher.match("http://www.host.com/**","http://www.host.com/abc"));
        Assertions.assertTrue(matcher.match("http://www.host.com/**","http://www.host.com/abc/"));
        Assertions.assertTrue(matcher.match("http://www.host.com/**","http://www.host.com/abc/bcd"));
        Assertions.assertTrue(matcher.match("http://www.host.com/**","http://www.host.com/abc/bcd/"));



        Assertions.assertTrue(matcher.match("http://www.host.com/*","http://www.host.com"));
        Assertions.assertTrue(matcher.match("http://www.host.com/*","http://www.host.com/"));
        Assertions.assertTrue(matcher.match("http://www.host.com/*","http://www.host.com/a"));
        Assertions.assertTrue(matcher.match("http://www.host.com/*","http://www.host.com/a/"));
        Assertions.assertFalse(matcher.match("http://www.host.com/*","http://www.host.com/a/b"));





    }
}