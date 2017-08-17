package com.charles.spider.scheduler.rule;

import com.charles.spider.transfer.entity.Rule;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by lq on 17-6-12.
 */
public class Domain {
    private final static Map<String, Domain> RULE_CHAINS = new HashMap<>();

    private String name;

    protected Domain parent = null;

    protected Map<String, Domain> child = new HashMap<>();
    protected List<Rule> rules = new ArrayList<>();


    public Domain(String name) {
        this.name = name;
    }


    public Domain(String name, Domain parent) {
        this.name = name;
        this.parent = parent;
    }

    public boolean equals(String domain) {
        return StringUtils.equals(name, domain);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Domain) {
            return equals(((Domain) obj).getName());
        }
        return super.equals(obj);
    }

    public String getName() {
        return name;
    }


    public Domain find(String domainName) {
        return child.get(domainName);
    }

    public Domain match(String host,boolean exact) {
        assert host != null;

        String[] blocks = host.split("\\.");

        if (!equals(blocks[blocks.length - 1])) return null;

        Domain it = this;

        for (int i = blocks.length - 2; i >= 0; i--) {
            Domain next = it.find(blocks[i]);
            if (next == null) {
                it = exact ? null : it;
                break;
            }
            it = next;
        }

        return it;
    }

    public Domain add(String host) {
        assert host != null;

        String[] blocks = host.split("\\.");

        if (!equals(blocks[blocks.length - 1])) return null;

        Domain it = this;

        for (int i = blocks.length - 2; i >= 0 && it != null; i--) {
            final String key = blocks[i];
            final Domain p = it;
            it = it.child.computeIfAbsent(key, k -> new Domain(key, p));
        }

        return it;
    }

    public void add(Domain domain) {


        Domain it = child.get(domain.getName());
        if (it != null)
            it.add(it.child.values());

        else {
            this.child.put(domain.getName(), domain);
            domain.parent = this;
        }
    }

    public void add(Collection<Domain> domains) {
        for (Domain it : domains)
            this.add(it);
    }


    public void addRule(Rule rule) {
        rules.add(rule);
        if (!RULE_CHAINS.containsKey(this.getName()))
            RULE_CHAINS.put(this.getName(), this);
    }

    public List<Rule> rules() {
        return rules;
    }


    public String host() {
        if (parent == null)
            return getName();

        String parentHost = parent.host();

        return StringUtils.isBlank(parentHost) ? getName() : getName() + "." + parentHost;

    }


}
