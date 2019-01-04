package com.bh.spider.scheduler.domain;

import java.net.URL;

public class RulePattern {
    private String originalPattern;
    private String host;

    public RulePattern(String pattern){
        this.originalPattern = pattern;

        try {
            URL u = new URL(pattern);
            host = u.getHost();
        }catch (Exception ignored){}
    }


    public String host() {
        return host;
    }
}
