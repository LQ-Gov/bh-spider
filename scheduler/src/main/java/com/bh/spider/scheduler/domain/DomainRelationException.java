package com.bh.spider.scheduler.domain;

public class DomainRelationException extends Exception {
    public DomainRelationException(String message) {
        super(message);
    }

    public DomainRelationException(String message, Throwable cause) {
        super(message, cause);
    }
}
