package com.bh.spider.scheduler.cluster;

import com.bh.spider.common.member.Node;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.BasicSchedulerRuleAssistant;
import com.bh.spider.scheduler.Config;
import com.bh.spider.scheduler.cluster.actuator.NodeCollection;
import com.bh.spider.scheduler.cluster.consistent.operation.Operation;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.DomainIndex;
import com.bh.spider.scheduler.domain.RuleConcrete;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.store.base.Store;

import java.io.IOException;

/**
 * @author liuqi19
 * @version ClusterSchedulerRuleAssistant, 2019-08-09 16:00 liuqi19
 **/
public class ClusterSchedulerRuleAssistant extends BasicSchedulerRuleAssistant {
    private ClusterScheduler scheduler;

    public ClusterSchedulerRuleAssistant(Config config, ClusterScheduler scheduler, Store store, DomainIndex domainIndex) throws Exception {
        super(config, scheduler, store, domainIndex);
        this.scheduler = scheduler;
    }

    /**
     * 集成SUBMIT_RULE_HANDLER
     *
     * @param ctx
     * @param rule
     * @throws Exception
     */
    @Override
    @CommandHandler(autoComplete = false)
    @Operation
    public void SUBMIT_RULE_HANDLER(Context ctx, Rule rule) throws Exception {
        super.SUBMIT_RULE_HANDLER(ctx, rule);

        ctx.commandCompleted(null);
    }


    @Override
    @CommandHandler(autoComplete = false)
    @Operation
    public void DELETE_RULE_HANDLER(Context ctx, long id) throws Exception {
        super.DELETE_RULE_HANDLER(ctx, id);
        ctx.commandCompleted(null);
    }


    @Override
    @CommandHandler(autoComplete = false)
    @Operation
    public void SCHEDULER_RULE_EXECUTOR_HANDLER(Context ctx, long id, boolean run) throws Exception {
        super.SCHEDULER_RULE_EXECUTOR_HANDLER(ctx, id, run);

        ctx.commandCompleted(null);
    }


    @Override
    @CommandHandler(autoComplete = false)
    public void EDIT_RULE_HANDLER(Context ctx, Rule rule) throws IOException {
        super.EDIT_RULE_HANDLER(ctx, rule);
        ctx.commandCompleted(null);
    }

    @Override
    protected boolean runnable(RuleConcrete concrete) {
        if (concrete.id() == 0) return true;
        NodeCollection collection = scheduler.masters();
        Node node = collection.consistentHash((int) (concrete.id() % Integer.MAX_VALUE));
        //进行一致性hash，如果判断等于自身，则开始执行
        return (node != null && node.getId() == scheduler.self().getId());
    }
}
