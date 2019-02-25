package com.bh.spider.common.fetch;

import java.util.List;
import java.util.Map;

public interface Response {

    int code();

    Map<String,String> headers();

    byte[] data();

    Cookie cookie(String name);

    List<Cookie> cookies();
}
