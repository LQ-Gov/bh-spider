package com.bh.spider.scheduler.fetcher;

import org.openqa.selenium.Cookie;

import java.net.HttpCookie;
import java.util.Date;

public class SeleniumCookieAdapter extends Cookie {

    public SeleniumCookieAdapter(HttpCookie cookie) {
        super(cookie.getName(), cookie.getValue(),
                cookie.getDomain().startsWith(".") ? cookie.getDomain() : "." + cookie.getDomain(),
                cookie.getPath(),
                cookie.getMaxAge() == -1 ? null : new Date(cookie.getMaxAge() * 1000),
                cookie.getSecure(),
                cookie.isHttpOnly());
    }
}
