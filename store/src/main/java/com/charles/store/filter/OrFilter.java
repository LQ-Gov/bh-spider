package com.charles.store.filter;

/**
 * Created by lq on 17-3-27.
 */
public class OrFilter extends Filter {
    private Filter content = null;
    public OrFilter(Filter filter) {
        this.content = filter;
    }
}
