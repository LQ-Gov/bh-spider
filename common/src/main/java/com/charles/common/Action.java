package com.charles.common;

/**
 * Created by lq on 17-3-21.
 */
public interface Action<T> {
    T exec(T... params);
}
