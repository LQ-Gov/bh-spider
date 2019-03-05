package com.bh.spider.scheduler.domain.pattern;

import java.util.Map;

public interface AntMatcher {

    boolean match(String value, Map<String, String> variables);

    String pattern();
}

