package com.charles.store.filter;

/**
 * Created by lq on 17-3-27.
 */
public class AndFilter extends Filter {
    private Filter content = null;
    public AndFilter(Filter filter){this.content=filter;}
}
