package com.bh.spider.common.fetch;

public interface Cookie {
    String getName();

    String getValue();

    String getComment();


    String getCommentURL();

    boolean isDiscard();

    String getDomain();

    long getMaxAge();

    String getPath();

    boolean isSecure();

    boolean isHttpOnly();

    int getVersion();
}
