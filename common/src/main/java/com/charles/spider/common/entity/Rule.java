package com.charles.spider.common.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-6-7.
 */
public class Rule {
    private String name;

    private Map<String,String[]> extractors= new HashMap<>();

    private String cron;

    private String host;

    private String pattern;

    private boolean exact;

    private String description;

    protected Rule(){}

    public Rule(String name,String host,String cron){
        this.name = name;
        this.host = host;
        this.cron = cron;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] extractor(String key) {
        return extractors.get(key);
    }

    public void extractor(String key,String[] chains) {
        this.extractors.put(key,chains);
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

    public void setHost(String host){
        this.host =host;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isExact() {
        return exact;
    }

    public void setExact(boolean exact) {
        this.exact = exact;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
