package com.charles.spider.common.rule;

import java.util.Map;

/**
 * Created by lq on 17-6-7.
 */
public class Rule {

    private String name;

    private Map<String,String[]> extractors;

    private String cron;

    private String host;

    private String pattern;

    protected Rule(){}

    public Rule(String name,String host,String cron){
        this.name = name;
        this.host = host;
        this.cron = cron;
    }


    public String getName() {
        return name;
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
}
