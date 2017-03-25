package com.charles.store.base;

/**
 * Created by lq on 17-3-24.
 */
public interface Statement {
    Statement where(Filters filter);


    <T> T exec();
}
