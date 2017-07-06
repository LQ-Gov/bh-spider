package com.charles.spider.scheduler.rule;

import com.charles.spider.scheduler.extractor.Extractor;

import java.util.Map;

/**
 * Created by lq on 7/5/17.
 */
public class RuleTemplate {
    private String name;

    private Map<String,String> extractors;

    private String cron;

    private String pattern;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getExtractors() {
        return extractors;
    }

    public void setExtractors(Map<String, String> extractors) {
        this.extractors = extractors;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }
}
