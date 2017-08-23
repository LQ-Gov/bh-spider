package com.charles.spider.fetch;

/**
 * Created by lq on 7/18/17.
 */
public enum HttpMethod {

    OPTIONS("OPTIONS"),
    GET("GET"),
    HEAD("HEAD"),
    POST("POST"),
    PUT("PUT"),
    PATCH("PATCH"),
    DELETE("DELETE"),
    TRACE("TRACE");


    private String content;

    HttpMethod(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return content;
    }
}
