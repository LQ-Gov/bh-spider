package com.bh.spider.scheduler.domain;

public class DomainNotFoundException extends Exception {

    public DomainNotFoundException(String message) {
        super(message);
    }

    public DomainNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
