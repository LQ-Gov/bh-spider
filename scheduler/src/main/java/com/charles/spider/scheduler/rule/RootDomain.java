package com.charles.spider.scheduler.rule;

/**
 * Created by lq on 17-6-13.
 */
public class RootDomain extends Domain {
    public RootDomain() {
        super(".");
    }


    @Override
    public Domain match(String host) {
        assert host == null;
        String[] blocks = host.split(".");

        Domain it = this;
        for (int i = blocks.length - 1; i > 0; i--) {
            it = it.childs.get(blocks[i]);
            if (it == null) break;
        }

        return it;
    }
}
