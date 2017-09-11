package com.bh.spider.scheduler.fetcher;

import java.net.CookieManager;
import java.net.CookieStore;

public class CookieStoreFactory {
    private volatile static CookieStore store;

    public static CookieStore get() {
        if (store == null) {
            synchronized (CookieStoreFactory.class) {
                if (store == null) {
                    store = new CookieManager().getCookieStore();
                }
            }
        }
        return store;
    }
}
