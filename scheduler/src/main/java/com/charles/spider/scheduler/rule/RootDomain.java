package com.charles.spider.scheduler.rule;

import java.util.*;

/**
 * Created by lq on 17-6-13.
 */
public class RootDomain extends Domain {
    public RootDomain() {
        super("");
    }


    @Override
    public Domain match(String host,boolean exact) {
        assert host != null;
        String[] blocks = host.split("\\.");

        Domain it = this;
        for (int i = blocks.length - 1; i >= 0; i--) {
            Domain next = it.child.get(blocks[i]);
            if (next == null) {
                it = exact ? null : it;
                break;
            }
            it = next;
        }

        return it;
    }

    @Override
    public Domain add(String host) {
        assert host != null;

        String[] blocks = host.split("\\.");

        Domain it = this;

        for (int i = blocks.length - 1; i >= 0; i--) {
            final String key = blocks[i];
            final Domain p = it;
            it = it.child.computeIfAbsent(key, k -> new Domain(key, p));
        }

        return it;
    }

    private List<String> hosts(Domain domain) {

        List<String> result = new LinkedList<>();
        if (domain.child.size() > 0) {
            for (Domain it : domain.child.values()) {
                result.addAll(hosts(it));
            }
        } else result.addAll(Collections.singletonList(domain.host()));

        return result;

    }


    public List<String> hosts() {
        return hosts(this);
    }
}
