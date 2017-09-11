package com.bh.spider.scheduler.fetcher;

import org.apache.http.cookie.Cookie;

import java.net.HttpCookie;
import java.util.Arrays;
import java.util.Date;

public class HttpClientCookieAdapter implements Cookie {

    private HttpCookie cookie;

    public HttpClientCookieAdapter(HttpCookie cookie) {
        this.cookie = cookie;
    }

    @Override
    public String getName() {
        return this.cookie.getName();
    }

    @Override
    public String getValue() {
        return this.cookie.getValue();
    }

    @Override
    public String getComment() {
        return this.cookie.getComment();
    }

    @Override
    public String getCommentURL() {
        return this.cookie.getCommentURL();
    }

    @Override
    public Date getExpiryDate() {
        return new Date(this.cookie.getMaxAge() * 1000);
    }

    @Override
    public boolean isPersistent() {
        return this.cookie.getDiscard();
    }

    @Override
    public String getDomain() {
        return this.cookie.getDomain();
    }

    @Override
    public String getPath() {
        return this.cookie.getPath();
    }

    @Override
    public int[] getPorts() {
        return Arrays.stream(cookie.getPortlist().split(","))
                .mapToInt(Integer::valueOf).toArray();
    }

    @Override
    public boolean isSecure() {
        return this.cookie.getSecure();
    }

    @Override
    public int getVersion() {
        return this.cookie.getVersion();
    }

    @Override
    public boolean isExpired(Date date) {
        return this.cookie.hasExpired();
    }
}
