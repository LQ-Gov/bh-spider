package com.bh.spider.scheduler.domain;

import com.bh.common.utils.CommandCode;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.fetcher.FetchContent;
import com.bh.spider.store.base.Store;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author liuqi19
 * @version RootRuleScheduleController, 2019-06-20 11:08 liuqi19
 **/
public class RootRuleScheduleController implements RuleScheduleController {
    private final static Logger logger = LoggerFactory.getLogger(DefaultRuleScheduleController.class);

    private Rule rule;
    private Scheduler scheduler;
    private Store store;

    private long offset;

    public RootRuleScheduleController(Scheduler scheduler, Rule rule, Store store) {
        this.rule = rule;
        this.scheduler = scheduler;
        this.store = store;
    }

    @Override
    public void execute() {
        scheduler.eventLoop().schedule(this::blast,this.rule.getCron());
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public Rule rule() {
        return rule;
    }

    @Override
    public void blast() throws Exception {
        if (!scheduler.running()) return;
        List<Request> requests = store.accessor().find(rule().getId(), Request.State.ASSIGNING, offset,10);
        if(CollectionUtils.isEmpty(requests)) {
            offset = 0;
        }
        else {
            Command cmd = new Command(new LocalContext(scheduler), CommandCode.SUBMIT_REQUEST_BATCH, requests);
            scheduler.process(cmd).get();
            offset+=requests.size();
        }




    }

    @Override
    public boolean joinQueue(Request request) {
        if(request.state()!= Request.State.ASSIGNING) {
            request = new FetchContent(request, Request.State.ASSIGNING);
            return store.accessor().save(request, rule.getId());
        }

        return false;
    }
}
