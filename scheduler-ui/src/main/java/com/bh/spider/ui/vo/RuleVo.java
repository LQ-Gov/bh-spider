package com.bh.spider.ui.vo;

import com.bh.spider.common.rule.Chain;
import com.bh.spider.common.rule.Rule;

import java.util.LinkedList;
import java.util.List;

public class RuleVo  {

    private String id;


    private List<Chain> chains = new LinkedList<>();
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


    private String[] policy;

    private boolean valid;


    public RuleVo(Rule rule) {
        this.setId(String.valueOf(rule.getId()));
        this.setCron(rule.getCron());
        this.setDescription(rule.getDescription());
        this.setChains(rule.chains());
        this.setParallelCount(rule.getParallelCount());
        this.setPattern(rule.getPattern());
        this.setPolicy(rule.getPolicy());
        this.setValid(rule.isValid());

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Chain> getChains() {
        return chains;
    }

    public void setChains(List<Chain> chains) {
        this.chains = chains;
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

    public int getParallelCount() {
        return parallelCount;
    }

    public void setParallelCount(int parallelCount) {
        this.parallelCount = parallelCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getPolicy() {
        return policy;
    }

    public void setPolicy(String[] policy) {
        this.policy = policy;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
