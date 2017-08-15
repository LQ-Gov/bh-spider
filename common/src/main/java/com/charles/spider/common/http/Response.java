package com.charles.spider.common.http;

import java.util.Map;

public interface Response {

    int statusCode();

    Map<String,String> headers();

    byte[] original();
}
