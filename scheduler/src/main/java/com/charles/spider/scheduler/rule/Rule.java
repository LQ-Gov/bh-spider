package com.charles.spider.scheduler.rule;

import com.charles.spider.scheduler.extractor.Extractor;

import java.util.Map;

/**
 * Created by lq on 17-6-7.
 */
public class Rule {

    private String name;

    private Map<String,Extractor> extractors;

    private String cron;

    private String host;

    private String pattern;

    private String pipeline;


    public Extractor extractor(String key) {
        return extractors.get(key);
    }


    public Rule extractor(String key,Extractor value) {
        extractors.put(key, value);
        return this;
    }


    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
