package com.bh.spider.scheduler;

import com.bh.common.utils.Json;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.*;
import com.bh.spider.scheduler.event.Assistant;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.store.base.Store;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class BasicSchedulerRuleAssistant implements Assistant {
    private final Map<Long, RuleFacade> FACADE_CACHE = new HashMap<>();
    private BasicScheduler scheduler;
    private DomainIndex domainIndex;


    private Store store;

    private Config cfg;


    public BasicSchedulerRuleAssistant(Config config, BasicScheduler scheduler, Store store, DomainIndex domainIndex) throws Exception {
        this.scheduler = scheduler;
        this.domainIndex = domainIndex;
        this.cfg = config;
        this.store = store;

        initLocalRuleController();

    }


    protected void initLocalRuleController() throws Exception {
        Path dir = Paths.get(cfg.get(Config.INIT_DATA_RULE_PATH));

        Rule defaultRule = new Rule(0,"**",cfg.get(Config.INIT_DEFAULT_RULE_CRON));
        FACADE_CACHE.put(defaultRule.getId(), root(defaultRule));


        //对自定义规则初始化
        List<Path> filePaths = Files.list(dir).filter(x -> x.toString().endsWith(".rule")).collect(Collectors.toList());

        for (Path filePath : filePaths) {
            List<Rule> rules = Json.get().readValue(filePath.toFile(), Json.constructCollectionType(ArrayList.class, Rule.class));

            for (Rule rule : rules) {
                RuleFacade facade = rule.isValid() ? facade(rule) : daemon(rule);
                FACADE_CACHE.put(facade.id(), facade);
            }
        }

    }


    private RuleFacade facade(Rule rule) throws Exception {
        return facade(rule, new DefaultRuleScheduleController(this.scheduler, rule, store));
    }

    /**
     * 将Rule组装为rule控制器,
     *
     * @param rule
     * @param ruleScheduleController
     * @return
     * @throws Exception
     */
    private RuleFacade facade(Rule rule, RuleScheduleController ruleScheduleController) throws Exception {
        if (rule.getId() <= 0) rule.setId(IdGenerator.instance.nextId());
        DefaultRuleFacade facade = new DefaultRuleFacade(this.scheduler, rule, ruleScheduleController);
        facade.link(this.domainIndex);

        if (facade.controller() != null && scheduler.running()) {
            facade.controller().execute();
        }

        return facade;
    }


    private RuleFacade daemon(Rule rule) throws Exception {
        rule.setCron("*/1 * * * * ?");
        RuleFacade facade = new DaemonRuleFacade(this.scheduler, rule, new DaemonRuleScheduleController(this.scheduler, rule, store));
        facade.link(domainIndex);
        if (facade.controller() != null && scheduler.running())
            facade.controller().execute();
        return facade;

    }

    private RuleFacade root(Rule rule){
        RuleFacade facade = new RootRuleFacade(scheduler,rule,new RootRuleScheduleController(scheduler,rule,store));
        facade.link(domainIndex);


        return facade;
    }


    private void backup(DomainIndex.Node node) throws IOException {
        String host = node.host();
        host = host == null ? "__ROOT__" : host;
        Path path = Paths.get(cfg.get(Config.INIT_DATA_RULE_PATH), host + ".rule");


        List<Rule> rules = node.rules().stream().map(RuleFacade::original).collect(Collectors.toList());

        Files.write(path, Json.get().writeValueAsBytes(rules));
    }


    @CommandHandler
    public void SUBMIT_RULE_HANDLER(Context ctx, Rule rule) throws Exception {
        if (validate(rule)) {
            RuleFacade facade = facade(rule);
            backup(facade.domainNode());
            FACADE_CACHE.put(facade.id(), facade);
        }
    }

    @CommandHandler
    public List<Rule> GET_RULE_LIST_HANDLER(Context ctx, String host) {

        Iterator<RuleFacade> iterator = FACADE_CACHE.values().iterator();

        List<Rule> result = new LinkedList<>();

        while (iterator.hasNext()) {
            result.add(iterator.next().original());
        }

        return result;
    }


    @CommandHandler
    public void DELETE_RULE_HANDLER(Context ctx, long id) throws Exception {
        RuleFacade facade = FACADE_CACHE.get(id);
        if (facade == null || !facade.modifiable()) return;

        facade.controller().close();

        facade.original().setValid(false);

        facade.domainNode().unbind(facade);

        backup(facade.domainNode());

        daemon(facade.original());


    }


    @CommandHandler
    public void TERMINATION_RULE_HANDLER(Context ctx, long id) throws Exception {
        RuleFacade facade = FACADE_CACHE.get(id);
        if (facade == null) return;

        Rule rule = facade.original();

        if (!rule.isValid()) {

            facade.controller().close();
            facade.domainNode().unbind(facade);

            //之所以守护规则还需要关联domain node,就是为了backup domainNode
            backup(facade.domainNode());

            FACADE_CACHE.remove(facade.id());
        }
    }

    @CommandHandler
    public RuleFacade RULE_FACADE_HANDLER(Context ctx, Rule rule) throws Exception {
        if (rule == null || rule.getId() <= 0) return null;

        RuleFacade facade = FACADE_CACHE.get(rule.getId());

        if (facade == null) {
            facade = facade(rule, null);
        }

        return facade;
    }

    @CommandHandler
    public void SCHEDULER_RULE_EXECUTOR_HANDLER(Context ctx, long id, boolean valid) throws Exception {
        RuleFacade decorator = FACADE_CACHE.get(id);
        if (decorator == null) return;
        if (valid)
            decorator.controller().execute();
        else
            decorator.controller().close();
    }

    private boolean validate(Rule rule) {
        if (rule == null) throw new IllegalArgumentException("input is null");
        if (StringUtils.isBlank(rule.getCron())) throw new IllegalArgumentException("cron can't empty");

        return true;
    }


    @Override
    public void initialized() {
        for (RuleFacade facade : FACADE_CACHE.values()) {
            if (facade.controller() != null)
                facade.controller().execute();
        }

    }
}
