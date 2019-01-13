package com.bh.spider.scheduler.domain;

import com.bh.spider.rule.Rule;

import java.util.Collection;
import java.util.function.Consumer;

public interface Domain {
    String nodeName();

    Domain find(String path);

    /**
     * 如果exact=false,则返回与其最匹配的一个，否则返回null
     * @param path
     * @param exact
     * @return
     */
    Domain find(String path,boolean exact);

    Collection<Domain> children();

    Domain children(String name);

    Domain parent();

    Domain put(String path);

    void delete(String path,boolean force) throws Exception;

    void delete(String path) throws Exception;

    String host();

    void bindRule(Rule rule);

    void unbindRule(Rule rule);

    Collection<Rule> rules();


    void ruleListener(Consumer<Rule> consumer);




}
