package com.bh.spider.scheduler.component;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author liuqi19
 * @version CommonClassLoader, 2019-06-18 18:14 liuqi19
 **/
public class CommonClassLoader extends URLClassLoader {
    public CommonClassLoader(URL[] urls) {
        super(urls);
    }
}
