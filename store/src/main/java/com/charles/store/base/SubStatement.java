package com.charles.store.base;

/**
 * Created by lq on 17-3-25.
 */
public interface SubStatement {
    Statement and(Filters filter);

    Statement or(Filters filter);
}
