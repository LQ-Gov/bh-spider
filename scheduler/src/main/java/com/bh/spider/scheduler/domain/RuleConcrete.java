package com.bh.spider.scheduler.domain;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.rule.Chain;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.domain.pattern.AntRulePattern;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuqi19
 * @version RuleConcrete, 2019-08-13 17:39 liuqi19
 **/
public class RuleConcrete {
    private long id;

    private AntRulePattern pattern;

    /**
     * 定时器
     */
    private String cron;

    private int parallelCount;

    private RuleScheduleController controller;

    private Rule rule;

    private List<Chain> chains;


    /**
     * 对此规则的描述
     */
    private String description;


    /**
     * 请求调度策略
     */
    private String[] policies;


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


    private boolean freeze;


    private boolean modifiable;


    public RuleConcrete(Rule rule, boolean modifiable) {
        this.update(rule);

        this.modifiable = modifiable;
    }


    public RuleConcrete(Rule rule) {
        this(rule, true);
    }


    public long id() {
        return id;
    }

    public boolean match(Request request) {
        return pattern.match(request.url());
    }

    public String host() {
        return pattern.host();
    }


    public RuleScheduleController controller() {
        return controller;
    }


    public void update(RuleScheduleController controller) {
        if (frozen()) return;
        final RuleScheduleController old = this.controller;
        this.controller = controller;

        if (old != null && old.running()) {
            old.close();
            this.controller.execute();
        }
    }


    public void update(RuleScheduleController controller, boolean foreExecute) {
        if (frozen()) return;
        update(controller);
        if (foreExecute && !this.controller.running())
            this.controller.execute();
    }

    public void update(Rule rule) {
        if (frozen()) return;
        this.pattern = new AntRulePattern(rule.getPattern());
        this.cron = rule.getCron();
        this.chains = new ArrayList<>(rule.chains());
        this.parallelCount = rule.getParallelCount();
        this.description = rule.getDescription();
        this.proxies = rule.getProxies();
        this.policies = rule.getPolicy();
        this.timeout = rule.getTimeout();
        this.repeat = rule.isRepeat();
        this.nodes = rule.getNodes();
        this.id = rule.getId();

        this.rule = rule;
    }


    public void execute() {
        if (this.controller != null)
            this.controller.execute();
    }

    public boolean modifiable() {
        return modifiable;
    }

    public boolean frozen() {
        return freeze;
    }

    public Rule base() {

        return rule;
    }


    public String cron() {
        return cron;
    }

    public void freeze() {
        this.freeze = true;
    }


    public RulePattern pattern() {
        return pattern;
    }

}
