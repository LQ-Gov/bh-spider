package com.bh.spider.scheduler;

import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.DefaultRuleScheduleController;
import com.bh.spider.scheduler.domain.DomainIndex;
import com.bh.spider.scheduler.domain.RuleFacade;
import com.bh.spider.scheduler.domain.RuleScheduleController;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IAssist;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.store.base.Store;
import com.bh.spider.transfer.Json;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class BasicSchedulerRuleHandler implements IAssist {
    private BasicScheduler scheduler;
    private JobCoreScheduler jobCoreScheduler;
    private DomainIndex domainIndex;
    private Map<Long, RuleFacade> facadeCache = new HashMap<>();
    private Store store;

    private Config cfg;


    public BasicSchedulerRuleHandler(Config config, BasicScheduler scheduler, Store store, JobCoreScheduler jobCoreScheduler, DomainIndex domainIndex) throws Exception {
        this.scheduler = scheduler;
        this.domainIndex = domainIndex;
        this.jobCoreScheduler = jobCoreScheduler;
        this.cfg = config;
        this.store = store;

        initLocalRuleController();
    }


    protected void initLocalRuleController() throws Exception {
        Path ruleDirectory = Paths.get(cfg.get(Config.INIT_DATA_RULE_PATH));


        List<Path> filePaths = Files.list(ruleDirectory).filter(x -> x.toString().endsWith(".json")).collect(Collectors.toList());

        for (Path filePath : filePaths) {
            List<Rule> rules = Json.get().readValue(filePath.toFile(),
                    Json.get().getTypeFactory().constructCollectionType(ArrayList.class, Rule.class));

            for (Rule rule : rules)
                facade(rule, true);
        }
    }


    private RuleFacade facade(Rule rule, boolean cached) throws Exception {

        return facade(rule, cached, new DefaultRuleScheduleController(this.scheduler, rule, store));
    }

    private RuleFacade facade(Rule rule, boolean cached, RuleScheduleController ruleScheduleController) throws Exception {
        if (rule.getId() <= 0) rule.setId(IdGenerator.instance.nextId());
        RuleFacade facade = new RuleFacade(this.scheduler, rule, ruleScheduleController);
        facade.link(this.domainIndex);

        if (facade.controller() != null)
            facade.controller().execute(jobCoreScheduler);
        if (cached)
            facadeCache.put(facade.id(), facade);

        return facade;
    }


    private void backup(DomainIndex.Node node) throws IOException {
        String host = node.host();
        host = host == null ? "__ROOT__" : host;
        Path path = Paths.get(cfg.get(Config.INIT_DATA_RULE_PATH), host + ".json");


        List<Rule> rules = node.rules().stream().map(RuleFacade::original).collect(Collectors.toList());

        Files.write(path, Json.get().writeValueAsBytes(rules));
    }


    @EventMapping
    protected void SUBMIT_RULE_HANDLER(Context ctx, Rule rule) throws Exception {
        if (validate(rule)) {
            RuleFacade boost = facade(rule, true);
            backup(boost.domainNode());
        }

    }

    @EventMapping
    protected List<Rule> GET_RULE_LIST_HANDLER(Context ctx, String host) {

        Iterator<RuleFacade> iterator = facadeCache.values().iterator();

        List<Rule> result = new LinkedList<>();

        while (iterator.hasNext()) {
            result.add(iterator.next().original());
        }

        return result;
    }


    @EventMapping
    protected void DELETE_RULE_HANDLER(Context ctx, long id) throws IOException {
        RuleFacade boost = facadeCache.get(id);
        if (boost == null) return;

        boost.destroy();
        boost.controller().close();
        facadeCache.remove(id);

        backup(boost.domainNode());
    }

    @EventMapping
    protected RuleFacade RULE_FACADE_HANDLER(Context ctx, Rule rule) throws Exception {
        if (rule == null || rule.getId() <= 0) return null;

        RuleFacade facade = facadeCache.get(rule.getId());

        if (facade == null) {
            facade = facade(rule, false, null);
        }

        return facade;
    }

    @EventMapping
    protected void SCHEDULER_RULE_EXECUTOR_HANDLER(Context ctx, long id, boolean valid) throws Exception {
        RuleFacade decorator = facadeCache.get(id);
        if (decorator == null) return;
        if (valid)
            decorator.controller().execute(jobCoreScheduler);
        else
            decorator.controller().close();
    }

    private boolean validate(Rule rule) {
        if (rule == null) throw new IllegalArgumentException("input is null");
        if (StringUtils.isBlank(rule.getCron())) throw new IllegalArgumentException("cron can't empty");

        return true;
    }
}
