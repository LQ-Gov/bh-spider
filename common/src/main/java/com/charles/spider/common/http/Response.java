package com.charles.spider.common.http;

import java.util.Map;

public interface Response {

    int code();

    Map<String,String> headers();

    byte[] data();
}
