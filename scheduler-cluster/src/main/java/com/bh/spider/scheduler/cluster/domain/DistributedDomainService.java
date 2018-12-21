package com.bh.spider.scheduler.cluster.domain;

import com.bh.spider.rule.Rule;
import com.bh.spider.scheduler.domain.Domain;
import com.bh.spider.scheduler.domain.RuleController;

import java.util.Collection;

public interface DistributedDomainService {

    String nodeName();

    Domain find(String path);

    Collection<Domain> children();

    Domain children(String name);

    Domain parent();

    void put(String path);

    void delete(String path, boolean force);

    void delete(String path);

    String host();

    void bindRule(Rule rule);

    Collection<Rule> rules();
}
