package com.charles.spider.common.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lq on 17-6-7.
 */
public class Rule {

    private String id;

    private Map<String,String[]> extractors= new HashMap<>();

    private String cron;

    private String host;

    private String pattern;

    private boolean exact;

    private int threadCount;

    private String dispatcher;

    private String description;

    protected Rule() {
        this.id = UUID.randomUUID().toString();
    }

    @JsonCreator
    public Rule(@JsonProperty("id") String id) {
        this.id = id;
        if (this.id == null || this.id.length() == 0) this.id = UUID.randomUUID().toString();
    }

    public Rule(String pattern,String host,String cron){
        this.pattern = pattern;
        this.host = host;
        this.cron = cron;
    }

    public String getId() {
        return id;
    }

    public String[] extractor(String key) {
        return extractors.get(key);
    }

    public void extractor(String key,String[] chains) {
        this.extractors.put(key,chains);
    }

    public Map<String,String[]> extractors(){return extractors;}

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
