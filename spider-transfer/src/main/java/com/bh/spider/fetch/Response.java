package com.bh.spider.fetch;

import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

public interface Response {

    int code();

    Map<String,String> headers();

    byte[] data();

    Cookie cookie(String name);

    List<Cookie> cookies();
}
