package com.bh.spider.scheduler;

import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.config.Config;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.domain.Domain;
import com.bh.spider.scheduler.domain.RuleController;
import com.bh.spider.scheduler.domain.RuleEnhance;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.IAssist;
import com.bh.spider.scheduler.job.JobCoreScheduler;
import com.bh.spider.store.base.Store;
import com.bh.spider.transfer.Json;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class BasicSchedulerRuleHandler implements IAssist {
    private BasicScheduler scheduler;
    private JobCoreScheduler jobCoreScheduler;
    private Domain root;
    private Map<Long, RuleEnhance> ruleCache = new HashMap<>();
    private Store store;

    private Config cfg;


    public BasicSchedulerRuleHandler(Config config, BasicScheduler scheduler, Store store, JobCoreScheduler jobCoreScheduler, Domain domain) throws Exception {
        this.scheduler = scheduler;
        this.root = domain;
        this.jobCoreScheduler = jobCoreScheduler;
        this.cfg = config;
        this.store = store;

        initLocalRuleController();
    }


    protected void initLocalRuleController() throws Exception {
        Path ruleDirectory = Paths.get(cfg.get(Config.INIT_DATA_RULE_PATH));


        List<Path> filePaths = Files.list(ruleDirectory).filter(x -> x.toString().endsWith(".json")).collect(Collectors.toList());

        for(Path filePath:filePaths ) {
            List<Rule> rules = Json.get().readValue(filePath.toFile(),
                    Json.get().getTypeFactory().constructCollectionType(ArrayList.class, Rule.class));

            for (Rule rule : rules)
                enhance(rule);

        }
    }


    private RuleEnhance enhance(Rule rule) throws Exception {

        String host = null;
        try {
            URL u = new URL(rule.getPattern());
            host = u.getHost();
        }catch (Exception ignored){}

        Domain d = host==null?root: root.put(host);

        RuleController controller = RuleController.build(rule, this.scheduler, this.store);
        RuleEnhance eh = new RuleEnhance(rule, controller,d);

        d.bindRule(eh);
        ruleCache.put(eh.id(),eh);

        eh.controller().execute(jobCoreScheduler);


        return eh;




    }





    private void backup(Domain domain) throws IOException {
        String host = domain.host();
        host = host == null ? "__ROOT__" : host;
        Path path = Paths.get(cfg.get(Config.INIT_DATA_RULE_PATH), host + ".json");
        List<Rule> rules = new ArrayList<>(domain.rules());

        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            if (rule instanceof RuleEnhance)
                rules.set(i, ((RuleEnhance) rule).original());
        }
        Files.write(path, Json.get().writeValueAsBytes(rules));
    }



    @EventMapping
    protected void SUBMIT_RULE_HANDLER(Context ctx, Rule rule) throws Exception {
        if (rule.id() <= 0)
            rule.setId(IdGenerator.instance.nextId());
        RuleEnhance eh = enhance(rule);
        backup(eh.domain());
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
    protected void DELETE_RULE_HANDLER(Context ctx,long id) {
        RuleEnhance decorator = ruleCache.get(id);
        if (decorator == null) return;


        decorator.domain().unbindRule(decorator);
        decorator.controller().close();
        ruleCache.remove(id);
    }

    @EventMapping
    protected void SCHEDULER_RULE_EXECUTOR_HANDLER(Context ctx,long id, boolean valid) throws Exception {
        RuleEnhance decorator = ruleCache.get(id);
        if (decorator == null) return;
        if (valid)
            decorator.controller().execute(jobCoreScheduler);
        else
            decorator.controller().close();
    }
}
