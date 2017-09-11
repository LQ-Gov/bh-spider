package com.bh.spider.common.protocol;

/**
 * Created by LQ on 2015/11/11.
 */
public interface Assemble {
    Token next() throws Exception;

    int pos();
}
