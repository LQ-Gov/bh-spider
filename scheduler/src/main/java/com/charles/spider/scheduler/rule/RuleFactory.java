package com.charles.spider.scheduler.rule;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 7/5/17.
 */
public class RuleFactory {
    private Map<String, RuleTemplate> templates = new HashMap<>();


    public RuleFactory(String... paths) throws IOException {

        for(String p:paths ){
            Path path = Paths.get(p);
            Preconditions.checkState(Files.exists(path), "the path of {} not exists", p);
            File file = new File(path.toString());
            loadTemplate(file.listFiles());
        }
    }

    private void loadTemplate(File[] files) throws IOException {
        for (File file : files) {
            String name = file.getName();
            if (name.endsWith(".template.json")) {
                byte[] data = Files.readAllBytes(file.toPath());
                RuleTemplate template = JSON.parseObject(data, RuleTemplate.class);

                Preconditions.checkNotNull(template.getName(), "you must define the name property");

                Preconditions.checkState(templates.containsKey(template.getName()), "the template %s is exists", template.getName());

                template.setName(name.substring(0, name.length() - ".template.json".length()));

            }
        }

    }


}
