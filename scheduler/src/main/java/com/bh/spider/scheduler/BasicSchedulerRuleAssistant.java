package com.bh.spider.scheduler;

import com.bh.common.utils.Json;
import com.bh.common.watch.Rank;
import com.bh.spider.common.fetch.Request;
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
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class BasicSchedulerRuleAssistant implements Assistant {
    private final Map<Long, RuleConcrete> CONCRETE_CACHE = new HashMap<>();
    private Scheduler scheduler;
    private DomainIndex domainIndex;

    private Store store;

    private Config cfg;


    public BasicSchedulerRuleAssistant(Config config, Scheduler scheduler, Store store, DomainIndex domainIndex) {
        this.scheduler = scheduler;
        this.domainIndex = domainIndex;
        this.cfg = config;
        this.store = store;
    }


    protected void initLocalRuleController() throws Exception {
        Path dir = Paths.get(cfg.get(Config.INIT_DATA_RULE_PATH));


        /**
         * 初始化默认的规则列表
         */
        Rule defaultRule = new Rule(0, "**", cfg.get(Config.INIT_DEFAULT_RULE_CRON));
        RuleConcrete defaultConcrete = new RuleConcrete(0, defaultRule, new RootRuleScheduleController(scheduler, defaultRule, store), false);

        domainIndex.root().bind(defaultConcrete);

        CONCRETE_CACHE.put(0L, defaultConcrete);


        //对自定义规则初始化
        List<Path> filePaths = Files.list(dir).filter(x -> x.toString().endsWith(".rule")).collect(Collectors.toList());

        for (Path filePath : filePaths) {
            List<Rule> rules = Json.get().readValue(filePath.toFile(), Json.constructCollectionType(ArrayList.class, Rule.class));


            for (Rule rule : rules) {
                RuleScheduleController controller = rule.isValid() ?
                        new DefaultRuleScheduleController(this.scheduler, rule, this.store)
                        : new DaemonRuleScheduleController(this.scheduler, rule, this.store);
                RuleConcrete concrete = new RuleConcrete(rule.getId(), rule, controller);

                domainIndex.matchOrCreate(concrete.host()).bind(concrete);
                CONCRETE_CACHE.put(concrete.id(), concrete);
            }

        }

        for (RuleConcrete concrete : CONCRETE_CACHE.values())
            if (runnable(concrete))
                concrete.execute();
    }


    protected void backup(DomainIndex.Node node) throws IOException {
        String host = node.host();
        host = host == null ? "__ROOT__" : host;
        Path path = Paths.get(cfg.get(Config.INIT_DATA_RULE_PATH), host + ".rule");


        List<Rule> rules = node.rules().stream().map(RuleConcrete::base).collect(Collectors.toList());

        Files.write(path, Json.get().writeValueAsBytes(rules));
    }

    protected boolean runnable(RuleConcrete concrete) {
        return true;
    }


    @CommandHandler
    public void SUBMIT_RULE_HANDLER(Context ctx, Rule rule) throws Exception {
        if (validate(rule)) {

            RuleConcrete concrete = new RuleConcrete(IdGenerator.instance.nextId(), rule,
                    new DefaultRuleScheduleController(this.scheduler, rule, this.store));

            DomainIndex.Node node = domainIndex.matchOrCreate(concrete.host()).bind(concrete);

            backup(node);

            if (runnable(concrete)) {
                CONCRETE_CACHE.put(concrete.id(), concrete);
                concrete.execute();
            }
        }
    }

    @CommandHandler
    public List<Rule> GET_RULE_LIST_HANDLER(Context ctx, String host) {

        Collection<RuleConcrete> concretes = CONCRETE_CACHE.values();

        return concretes.stream().map((RuleConcrete::base)).collect(Collectors.toList());
    }

    @CommandHandler
    public Rule GET_RULE_HANDLER(long id) {
        RuleConcrete concrete = CONCRETE_CACHE.get(id);

        return concrete == null ? null : concrete.base();
    }


    @CommandHandler
    public Rank GET_RULE_RANK(Context ctx, Request.State state, int size) throws SQLException {
        Rank rank = store.accessor().rank(state, size);

        return rank;

    }


    @CommandHandler
    public void EDIT_RULE_HANDLER(Context ctx, Rule rule) throws IOException {

        RuleConcrete concrete = CONCRETE_CACHE.get(rule.getId());

        if (concrete == null || !concrete.modifiable()) return;

        Rule old = concrete.base();


        boolean bind = true;
        if (!old.getPattern().equals(rule.getPattern())) {
            domainIndex.match(concrete.host()).unbind(concrete);
            if (runnable(concrete)) {
                store.accessor().reset(concrete.id());
            }
            bind = false;
        }

        concrete.update(rule);

        if (!old.getCron().equals(concrete.cron())) {
            concrete.update(new DefaultRuleScheduleController(scheduler, concrete.base(), store));
        }

        DomainIndex.Node node = null;
        if (!bind) {
            node = domainIndex.matchOrCreate(concrete.host()).bind(concrete);
        }
        if (node == null) node = domainIndex.match(concrete.host());

        backup(node);
    }

    @CommandHandler
    public void DELETE_RULE_HANDLER(Context ctx, long id) throws Exception {

        RuleConcrete concrete = CONCRETE_CACHE.get(id);
        if (concrete == null || !concrete.modifiable()) return;


        concrete.update(new DaemonRuleScheduleController(scheduler, concrete.base(), store), true);

        concrete.freeze();

        DomainIndex.Node node = domainIndex.match(concrete.host());

        backup(node);
    }


    @CommandHandler
    public void TERMINATION_RULE_HANDLER(Context ctx, long id) throws Exception {
        RuleConcrete concrete = CONCRETE_CACHE.get(id);
        if (concrete == null || !concrete.frozen()) return;

        DomainIndex.Node node = domainIndex.match(concrete.host()).unbind(concrete);

        backup(node);

        concrete.controller().close();

        CONCRETE_CACHE.remove(id);
    }


    @CommandHandler
    public void SCHEDULER_RULE_EXECUTOR_HANDLER(Context ctx, long id, boolean runnable) throws Exception {
        RuleConcrete concrete = CONCRETE_CACHE.get(id);

        if (concrete == null || concrete.frozen() || !runnable(concrete)) return;

        RuleScheduleController controller = concrete.controller();

        if (runnable && !controller.running())
            controller.running();
        else if (!runnable && controller.running())
            controller.close();
    }

    private boolean validate(Rule rule) {
        if (rule == null) throw new IllegalArgumentException("input is null");
        if (StringUtils.isBlank(rule.getCron())) throw new IllegalArgumentException("cron can't empty");

        return true;
    }


    @Override
    public void initialized() {
        try {
            initLocalRuleController();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
