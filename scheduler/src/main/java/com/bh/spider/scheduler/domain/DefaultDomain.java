package com.bh.spider.scheduler.domain;

import com.bh.spider.rule.Rule;

import java.util.Collection;
import java.util.function.Consumer;

public class DefaultDomain implements Domain {
    @Override
    public String nodeName() {
        return null;
    }

    @Override
    public Domain find(String path) {
        return null;
    }

    @Override
    public Domain find(String path, boolean exact) {
        return null;
    }

    @Override
    public Collection<Domain> children() {
        return null;
    }

    @Override
    public Domain children(String name) {
        return null;
    }

    @Override
    public Domain parent() {
        return null;
    }

    @Override
    public Domain put(String path) {
        return null;
    }

    @Override
    public void delete(String path, boolean force) throws Exception {

    }

    @Override
    public void delete(String path) throws Exception {

    }

    @Override
    public String host() {
        return null;
    }

    @Override
    public void bindRule(Rule rule) {

    }

    @Override
    public void unbindRule(Rule rule) {

    }

    @Override
    public Collection<Rule> rules() {
        return null;
    }

    @Override
    public void ruleListener(Consumer<Rule> consumer) {

    }
}
