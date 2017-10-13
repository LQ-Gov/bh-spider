package com.bh.spider.scheduler.rule;

import com.bh.spider.transfer.JsonFactory;
import com.bh.spider.transfer.entity.Rule;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lq on 7/5/17.
 */
public class RuleFactory {

    private Path base = null;

    //private List<Rule> rules = new ArrayList<>();


    private Map<String, List<Rule>> map = new ConcurrentHashMap<>();


    public RuleFactory(String p) throws IOException {

        this.base = Paths.get(p);

        Preconditions.checkState(Files.exists(base), "the path of %s not exists", p);

        File file = new File(base.toString());
        if (file.isDirectory())
            load(file.listFiles());
        else
            load(new File[]{file});

    }
    private void load(File[] files) throws IOException {
        if (files == null) return;
        for (File file : files) {

            byte[] data = Files.readAllBytes(file.toPath());

            JsonNode node = JsonFactory.get().readTree(data);

            if (node.isArray()) {

                List<Rule> rules = JsonFactory.get().readValue(node.traverse(), JsonFactory.get().getTypeFactory().constructCollectionType(List.class, Rule.class));
                rules.forEach(this::cache);
            } else if (node.isObject()) {
                Rule rule = JsonFactory.get().readValue(node.traverse(), Rule.class);
                cache(rule);
            }
        }
    }


    public List<Rule> get() {

        List<Rule> result = new LinkedList<>();

        map.values().forEach(result::addAll);

        return result;
    }

    public void save(Rule rule) throws IOException {
        List<Rule> values = cache(rule);

        save(rule.getHost() + ".json", values);


    }


    public void delete(Rule rule) throws IOException {
        String host = rule.getHost();

        List<Rule> rules = map.get(host);

        Iterator<Rule> it = rules.iterator();

        while (it.hasNext()) {
            Rule tmp = it.next();
            if (tmp.getId().equals(rule.getId())) {
                it.remove();
                save(host + ".json", rules);
            }
        }

    }


    private List<Rule> cache(Rule rule) {
        String host = rule.getHost();

        List<Rule> rules = map.computeIfAbsent(host, k -> new ArrayList<>());

        if(rules.isEmpty()||!rules.contains(rule))
            rules.add(rule);

        return rules;
    }


    private void save(String filename, List<Rule> rules) throws IOException {
        Path path = base;
        if (Files.isDirectory(base))
            path = Paths.get(base.toString(), filename);


        byte[] data = JsonFactory.get().writerWithDefaultPrettyPrinter().writeValueAsBytes(rules);
        Files.write(path, data);
    }


}
