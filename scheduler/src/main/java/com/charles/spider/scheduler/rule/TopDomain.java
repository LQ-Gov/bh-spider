package com.charles.spider.scheduler.rule;

/**
 * Created by lq on 17-6-13.
 */
public class TopDomain extends Domain {
    public TopDomain() {
        super(".");
    }


    @Override
    public Domain match(String host) {
        assert host != null;
        String[] blocks = host.split(".");

        Domain it = this;
        for (int i = blocks.length - 1; i > 0; i--) {
            it = it.child.get(blocks[i]);
            if (it == null) break;
        }

        return it;
    }
}
