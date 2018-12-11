package com.bh.spider.scheduler.domain;

import com.bh.spider.rule.Rule;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;

public class BasicDomain implements Domain {
    private final static Domain EMPTY_DOMAIN = new BasicDomain();

    private String nodeName;
    private Map<String, Domain> children;
    private Domain parent;

    private List<Rule> rules;

    private transient Map<String,List<Consumer<Rule>>> listeners = new HashMap<>();

    private boolean valid;

    private BasicDomain() {
    }


    public BasicDomain(String nodeName, Domain parent) {
        this.nodeName = nodeName;
        this.parent = (parent == null ? EMPTY_DOMAIN : parent);
    }

    @Override
    public String nodeName() {
        return nodeName;
    }

    @Override
    public Domain find(String path) {
        if (StringUtils.isBlank(path)) return null;

        String[] nodes = path.split("\\.");
        String last = nodes[nodes.length - 1];
        if (!(nodeName.equals(last) || "".equals(last))) return null;

        Domain it = this;
        for (int i = nodes.length - 2; it != null && i >= 0; i--) {
            it = it.children(nodes[i]);
        }

        return it;

    }

    @Override
    public Collection<Domain> children() {
        return children == null ? null : Collections.unmodifiableCollection(children.values());
    }

    @Override
    public Domain children(String name) {
        return children == null ? null : children.get(name);
    }

    @Override
    public Domain parent() {
        return parent;
    }

    @Override
    public Domain put(String path) {
        if (StringUtils.isBlank(path)) return this;

        String[] nodes = path.split("\\.");

        int last = nodes.length - 1;
        if (nodes[nodes.length - 1].equals(nodeName)) last -= 1;


        if (children == null) children = new HashMap<>();

        Domain it = this;
        for (int i = last; i >= 0; i--) {
            String name = nodes[i];
            if (StringUtils.isBlank(name)) continue;
            final Domain p = it;
            it = children.computeIfAbsent(name, x -> new BasicDomain(name, p));
        }
        return it;
    }

    @Override
    public void delete(String path, boolean force) throws Exception {
        if (StringUtils.isBlank(path)) return;


        Domain item = find(path);
        if (item == null) throw new DomainNotFoundException("未找到对应的domain");
        if (!force && item.children() != null) throw new DomainRelationException("该domain还有子项，无法强制删除");

        if (EMPTY_DOMAIN == item.parent()) return;

        item.parent().delete(nodeName(), true);
    }

    @Override
    public void delete(String path) throws Exception {
        delete(path, false);
    }

    @Override
    public String host() {
        if (parent == null || parent.host() == null)
            return nodeName();

        String parentHost = parent.host();

        return StringUtils.isBlank(parentHost) ? nodeName() : nodeName() + "." + parentHost;
    }

    @Override
    public void bindRule(Rule rule) {
        if (rules == null) rules = new ArrayList<>();
        rules.add(rule);
    }

    @Override
    public void unbindRule(Rule rule) {

    }

    @Override
    public Collection<Rule> rules() {
        return rules;
    }

    @Override
    public void ruleListener(Consumer<Rule> consumer) {
        listeners.computeIfAbsent("RULE", x -> new LinkedList<>()).add(consumer);
    }
}
