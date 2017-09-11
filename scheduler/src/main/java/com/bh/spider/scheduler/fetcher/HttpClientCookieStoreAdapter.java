package com.bh.spider.scheduler.fetcher;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;

import java.net.HttpCookie;
import java.net.URI;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class HttpClientCookieStoreAdapter implements CookieStore {
    private java.net.CookieStore cs = null;

    public HttpClientCookieStoreAdapter(java.net.CookieStore cs) {
        this.cs = cs;
    }

    @Override
    public void addCookie(Cookie cookie) {
        cs.add(null, toHttpCookie(cookie));
    }

    @Override
    public List<Cookie> getCookies() {
        return cs.getCookies().stream().map(HttpClientCookieAdapter::new)
                .collect(Collectors.toList());
    }

    public java.net.CookieStore original() {
        return cs;
    }

    @Override
    public boolean clearExpired(Date date) {
        List<HttpCookie> list = cs.getCookies();

        Iterator<HttpCookie> it = list.iterator();

        while (it.hasNext()) {
            HttpCookie cookie = it.next();
            if (cookie.hasExpired())
                cs.remove(null, cookie);
        }
        return true;
    }

    @Override
    public void clear() {
        cs.removeAll();
    }


    private HttpCookie toHttpCookie(Cookie cookie) {
        HttpCookie httpCookie = new HttpCookie(cookie.getName(), cookie.getValue());
        httpCookie.setComment(cookie.getComment());
        httpCookie.setCommentURL(cookie.getCommentURL());
        httpCookie.setDiscard(cookie.isPersistent());
        httpCookie.setDomain(cookie.getDomain());
        httpCookie.setMaxAge(cookie.getExpiryDate()==null?-1:cookie.getExpiryDate().getTime() / 1000);
        httpCookie.setPath(cookie.getPath());
        httpCookie.setVersion(cookie.getVersion());
        httpCookie.setSecure(cookie.isSecure());
        httpCookie.setPortlist(StringUtils.join(cookie.getPorts(), ","));
        return httpCookie;
    }
}
