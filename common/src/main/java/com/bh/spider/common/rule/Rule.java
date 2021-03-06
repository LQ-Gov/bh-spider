package com.bh.spider.common.rule;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.*;

/**
 * Created by lq on 17-6-7.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS,include = JsonTypeInfo.As.EXISTING_PROPERTY,property = "_class")
public class Rule implements Serializable {

    private final Class<?> _class = Rule.class;

    private long id;

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


    /**
     * 请求调度策略
     */
    private String[] policy;


    /**
     * 代理
     */
    private String[] proxies;


    private int timeout;


    private boolean repeat;


    /**
     * 分发的节点
     */
    private String[] nodes;

    private boolean valid;

    private boolean running;

    public Rule() {
        this(0);
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


    public Rule(long id, Rule rule) {
        this.id = id;
        this.chains = rule.chains;
        this.cron = rule.cron;
        this.pattern = rule.pattern;
        this.parallelCount = rule.parallelCount;
        this.description = rule.description;
        this.policy = rule.policy;
        this.proxies = rule.proxies;
        this.timeout = rule.timeout;
        this.repeat = rule.repeat;
        this.nodes = rule.nodes;
        this.valid = rule.valid;
        this.running = rule.running;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
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
        return obj instanceof Rule && (this == obj || this.getId() == ((Rule) obj).getId());
    }

    public int getParallelCount() {
        return parallelCount;
    }

    public void setParallelCount(int parallelCount) {
        this.parallelCount = parallelCount;
    }

    public List<Chain> chains() {
        return chains;
    }

    public void setChains(List<Chain> chains) {
        this.chains = chains;
    }

    public String[] getPolicy() {
        return policy;
    }

    public void setPolicy(String[] policy) {
        this.policy = policy;
    }

    public String[] getProxies() {
        return proxies;
    }

    public void setProxies(String[] proxies) {
        this.proxies = proxies;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String[] getNodes() {
        return nodes;
    }

    public void setNodes(String[] nodes) {
        this.nodes = nodes;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }


    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
