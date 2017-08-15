package com.charles.spider.common.http;

import com.charles.spider.common.extractor.Document;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

public interface FetchContext {


    URL url();

    Request request();

    Response response();

    Document document();

    Document document(Charset charset);

    void set(String key,Object value);

    void set(Map<String,Object> collection);

    Object get(String key);


    void scheduler(FetchContext ctx, Request req, boolean local);

    void scheduler(FetchContext ctx, Request req);

    void scheduler(Request req);


    void scheduler(FetchContext ctx, String url, boolean local);

    void scheduler(FetchContext ctx, String url);

    void scheduler(String url);


    void cancel();




}
