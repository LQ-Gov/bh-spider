package com.bh.spider.fetch;

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
