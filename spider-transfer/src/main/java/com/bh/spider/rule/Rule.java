package com.bh.spider.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lq on 17-6-7.
 */
public class Rule {

    private long id;

    private List<ExtractorGroup> extractors = new ArrayList<>();
    /**
     * 定时器
     */
    private String cron;
    /**
     * URL匹配规则
     */
    private String pattern;

    private int parallelCount;
    /**
     * 对此规则的描述
     */
    private String description;

    private boolean valid;

    public Rule() {
    }

    public Rule(long id) {
        this(id, null, null);
    }

    public Rule(String pattern, String cron) {
        this(0, pattern, cron);
    }

    public Rule(long id, String pattern, String cron) {
        this.id = id;
        this.pattern = pattern;
        this.cron = cron;
        this.valid = true;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long id() {
        return id;
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

    public int getParallelCount() {
        return parallelCount;
    }

    public void setParallelCount(int parallelCount) {
        this.parallelCount = parallelCount;
    }


    public ExtractorGroup extractorGroup(int index){
        return extractors.get(index);
    }

    public List<ExtractorGroup> extractorGroups(){return extractors;}


    public void addExtractorGroup(ExtractorGroup group){
        extractors.add(group);
    }


}
