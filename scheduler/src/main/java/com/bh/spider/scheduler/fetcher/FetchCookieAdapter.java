package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.impl.FetchCookie;

import java.net.HttpCookie;

public class FetchCookieAdapter extends FetchCookie {

    public FetchCookieAdapter(HttpCookie cookie){
        this.setComment(cookie.getComment());
        this.setName(cookie.getName());
        this.setCommentURL(cookie.getCommentURL());
        this.setDiscard(cookie.getDiscard());
        this.setDomain(cookie.getDomain());
        this.setPath(cookie.getPath());
        this.setHttpOnly(cookie.isHttpOnly());
        this.setSecure(cookie.getSecure());
        this.setValue(cookie.getValue());
        this.setVersion(cookie.getVersion());
        this.setMaxAge(cookie.getMaxAge());
    }
}
