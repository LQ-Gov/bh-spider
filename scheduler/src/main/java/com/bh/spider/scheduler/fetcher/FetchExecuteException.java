package com.bh.spider.scheduler.fetcher;

public class FetchExecuteException extends Exception {

    public FetchExecuteException(Throwable cause){
        super(cause);
    }

    public FetchExecuteException(String message,Throwable cause){
        super(message,cause);
    }
}
