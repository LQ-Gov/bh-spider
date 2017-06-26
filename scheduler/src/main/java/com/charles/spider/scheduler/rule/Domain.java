package com.charles.spider.scheduler.rule;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by lq on 17-6-12.
 */
public class Domain {
    private final static Map<String,Domain> RULE_CHAINS = new HashMap<>();

    private String name;

    protected Domain parent = null;

    protected Map<String,Domain> childs = new HashMap<>();
    protected List<Rule> rules = new ArrayList<>();



    public Domain(String name){
        this.name = name;
    }

    public boolean equals(String domain) {
        return StringUtils.equals(name, domain);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Domain) {
            return equals(((Domain) obj).getName());
        }
        return super.equals(obj);
    }

    public String getName() {
        return name;
    }


    public Domain find(String domainName){
        return childs.get(domainName);
    }

    public Domain match(String host){
        assert host==null;

        String[] blocks = host.split(".");

        if(!equals(blocks[blocks.length-1])) return null;

        Domain cur = this;

        for(int i=blocks.length-2;i>0;i--) {
            cur = cur.find(blocks[i]);
            if (cur == null) break;
        }

        return cur;
    }

    public Domain add(String host) {
        assert host != null;

        String[] blocks = host.split(".");

        if (!equals(blocks[0])) return null;

        Domain it = this;

        for (int i = 1; i < blocks.length && it != null; i++) {
            it = it.childs.get(blocks[i]);
        }

        return it;
    }

    public void add(Domain domain) {


        Domain it = childs.get(domain.getName());
        if (it != null)
            it.add(it.childs.values());

        else {
            this.childs.put(domain.getName(), domain);
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


    public String host() {
        if (parent == null)
            return getName();

        return getName() + "." + parent.host();
    }


}
