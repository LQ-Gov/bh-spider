package com.bh.spider.scheduler;

import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.Domain;
import com.bh.spider.scheduler.domain.RuleController;
import com.bh.spider.scheduler.domain.RuleDecorator;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IAssist;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.scheduler.rule.RuleBindException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SchedulerRuleHandler implements IAssist {
    private BasicScheduler scheduler;
    private JobCoreScheduler jobCoreScheduler;
    private Domain root;
    private Map<String,RuleDecorator> ruleCache = new HashMap<>();



    public SchedulerRuleHandler(BasicScheduler scheduler, JobCoreScheduler jobCoreScheduler, Domain domain){
        this.scheduler=scheduler;
        this.root = domain;
        this.jobCoreScheduler = jobCoreScheduler;

        foreachRules(root);
    }


    private void foreachRules(Domain domain) {
        if (domain == null) return;

        Collection<Rule> rules = domain.rules();
        if (rules != null) {
            for (Rule rule : rules) {
                RuleDecorator decorator = (RuleDecorator) rule;
                ruleCache.put(decorator.id(), decorator);
            }
        }
        if (domain.children() != null)
            domain.children().forEach(this::foreachRules);
    }



    @EventMapping
    protected void SUBMIT_RULE_HANDLER(Context ctx, Rule rule) throws RuleBindException {
        Domain domain = root.put(rule.getHost());
        RuleController controller = RuleController.build(rule, scheduler, domain);
        RuleDecorator decorator = new RuleDecorator(rule, controller,domain);
        domain.bindRule(decorator);
    }

    @EventMapping
    protected void GET_RULE_LIST_HANDLER(Context ctx, String host, int skip, int size) {


        ctx.write(ruleCache.values());

//        List<Rule> rules = new LinkedList<>();
//
//        if (StringUtils.isBlank(host)) {
//            Stack<Domain> stack = new Stack<>();
//            stack.add(root);
//
//            while (!stack.isEmpty()) {
//                com.bh.spider.scheduler.rule.Domain it = stack.pop();
//                rules.addAll(it.rules().stream().map(x -> ((com.bh.spider.scheduler.rule.RuleDecorator) x).original()).collect(Collectors.toList()));
//                stack.addAll(it.children());
//
//            }
//
//        } else {
//
//            Domain domain = root.find(host);
//
//             domain.rules()
//
//            com.bh.spider.scheduler.rule.Domain matcher = domain.match(host, true);
//
//            rules = matcher == null ? rules : matcher.rules();
//        }
//
//        if (size < 0) size = Math.max(rules.size() - skip, 0);
//
//        rules = rules.subList(skip, Math.min(skip + size, rules.size()));
//
//        ctx.write(rules);
    }


    @EventMapping
    protected void DELETE_RULE_HANDLER(Context ctx,String id) {
        RuleDecorator decorator = ruleCache.get(id);
        if (decorator == null) return;


        decorator.domain().unbindRule(decorator);
        decorator.controller().close();
        ruleCache.remove(id);
    }

    @EventMapping
    protected void SCHEDULER_RULE_EXECUTOR_HANDLER(Context ctx,String id, boolean valid) throws Exception {
        RuleDecorator decorator = ruleCache.get(id);
        if (decorator == null) return;
        if (valid)
            decorator.controller().execute(jobCoreScheduler);
        else
            decorator.controller().close();
    }
}
