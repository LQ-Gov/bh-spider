package com.charles.store.stantment;

import com.charles.store.filter.Filter;

/**
 * Created by lq on 17-3-24.
 */
public interface Statement {
    SubStatement where(Filter filter);


    <T> T exec();
}
