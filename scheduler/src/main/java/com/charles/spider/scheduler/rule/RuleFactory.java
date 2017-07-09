package com.charles.spider.scheduler.rule;

import com.alibaba.fastjson.JSON;
import com.charles.spider.common.rule.Rule;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lq on 7/5/17.
 */
public class RuleFactory {
    //    private Map<String, RuleTemplate> templates = new HashMap<>();
    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }


    private Path base = null;

    //private List<Rule> rules = new ArrayList<>();


    private Map<String, List<Rule>> map = new ConcurrentHashMap<>();



    public RuleFactory(String p) throws IOException {

        this.base = Paths.get(p);

        Preconditions.checkState(Files.exists(base), "the path of {} not exists", p);

        File file = new File(base.toString());
        if (file.isDirectory())
            load(file.listFiles());
        else
            load(new File[]{file});

    }

    private void loadTemplate(File[] files) throws IOException {
        for (File file : files) {
            String name = file.getName();
            if (name.endsWith(".template.json")) {
                byte[] data = Files.readAllBytes(file.toPath());
                RuleTemplate template = JSON.parseObject(data, RuleTemplate.class);

                Preconditions.checkNotNull(template.getName(), "you must define the name property");

                //Preconditions.checkState(templates.containsKey(template.getName()), "the template %s is exists", template.getName());

                template.setName(name.substring(0, name.length() - ".template.json".length()));

            }
        }

    }

    private void load(File[] files) throws IOException {
        if(files==null) return;
        for (File file : files) {

            byte[] data = Files.readAllBytes(file.toPath());

            JsonNode node = mapper.readTree(data);

            if (node.isArray()) {

                List<Rule> rules = mapper.readValue(node.traverse(), mapper.getTypeFactory().constructCollectionType(List.class, Rule.class));
                rules.forEach(this::cache);
            } else if (node.isObject()) {
                Rule rule = mapper.readValue(node.traverse(), Rule.class);
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

        save(rule.getHost()+".json",values);



    }


    private List<Rule> cache(Rule rule) {
        String host = rule.getHost();

        List<Rule> rules = map.computeIfAbsent(host, k -> new ArrayList<>());

        rules.add(rule);

        return rules;
    }




    private void save(String filename, List<Rule> rules) throws IOException {
        Path path = base;
        if (Files.isDirectory(base))
            path = Paths.get(base.toString(), filename);


        byte[] data =mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(rules);
        Files.write(path,data);
    }


}
