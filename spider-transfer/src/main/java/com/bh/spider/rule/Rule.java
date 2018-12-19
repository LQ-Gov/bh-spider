package com.bh.spider.rule;

import java.nio.file.PathMatcher;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-6-7.
 */
public class Rule {

    private long id;

    private Map<String, String[]> extractors = new HashMap<>();

    /**
     * 定时器
     */
    private String cron;

    private String host;

    /**
     * URL匹配规则
     */
    private String pattern;

    private transient PathMatcher matcher = null;

    /**
     * 精确匹配?
     */
    private boolean exact;

    private int parallelCount;

    private String dispatcher;

    /**
     * 对此规则的描述
     */
    private String description;

    private boolean valid;

    public Rule() {
    }

    public Rule(long id) {
        this(id, null, null, null);
    }

    public Rule(String pattern, String host, String cron) {
        this(0, pattern, host, cron);
    }

    public Rule(long id, String pattern, String host, String cron) {
        this.id = id;
        this.pattern = pattern;
        this.host = host;
        this.cron = cron;
        this.valid = true;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long id() {
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
        return obj instanceof Rule && (this == obj || this.id() == ((Rule) obj).id());
    }

    public String getDispatcher() {
        return dispatcher;
    }

    public void setDispatcher(String dispatcher) {
        this.dispatcher = dispatcher;
    }

    public int getParallelCount() {
        return parallelCount;
    }

    public void setParallelCount(int parallelCount) {
        this.parallelCount = parallelCount;
    }
}
