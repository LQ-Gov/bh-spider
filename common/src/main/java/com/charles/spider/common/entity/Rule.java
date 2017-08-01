package com.charles.spider.common.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lq on 17-6-7.
 */
public class Rule {

    private String id;

    private Map<String, String[]> extractors = new HashMap<>();

    private String cron;

    private String host;

    private String pattern;

    private boolean exact;

    private int taskCount;

    private String dispatcher;

    private String description;

    private boolean valid;

    protected Rule() {
        this(UUID.randomUUID().toString());
    }

    @JsonCreator
    public Rule(@JsonProperty("id") String id) {
        this(id, null, null, null);
    }

    public Rule(String pattern, String host, String cron) {
        this(UUID.randomUUID().toString(), pattern, host, cron);
    }

    public Rule(String id, String pattern, String host, String cron) {
        id = StringUtils.isBlank(id) ? UUID.randomUUID().toString() : id;
        this.id = id;
        this.pattern = pattern;
        this.host = host;
        this.cron = cron;
        this.valid = true;
    }

    public String getId() {
        return id;
    }

    public String[] extractor(String key) {
        return extractors.get(key);
    }

    public void extractor(String key, String[] chains) {
        this.extractors.put(key, chains);
    }

    public Map<String, String[]> extractors() {
        return extractors;
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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof Rule) {
            return this == obj || this.getId().equals(((Rule) obj).getId());
        }
        return false;
    }
}
