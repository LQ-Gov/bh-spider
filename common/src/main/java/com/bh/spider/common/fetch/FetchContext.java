package com.bh.spider.common.fetch;

import com.bh.spider.common.rule.Rule;
import com.bh.spider.doc.Document;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

public interface FetchContext {

    URL url();

    Request request();

    Response response();

    Rule rule();

    Cookie cookie(String name);

    List<Cookie> cookies();



    Document document();

    Document document(Charset charset);

    void set(String key, Object value);

    void set(Map<String, Object> collection);

    Object get(String key);

    Object get(String key,Object defaultValue);

    Map<String,Object> fields();


    void schedule(FetchContext ctx, Request req, boolean local) throws Exception;

    void schedule(FetchContext ctx, Request req) throws Exception;

    void schedule(Request req) throws Exception;


    void schedule(FetchContext ctx, String url, boolean local) throws Exception;

    void schedule(FetchContext ctx, String url) throws Exception;

    void schedule(String url) throws Exception;


    void schedule(FetchContext ctx, List<Request> requests, boolean local) throws Exception;


    void schedule(FetchContext ctx, List<String> requests) throws Exception;


    void schedule(List<String> urls) throws Exception;

    void termination() throws ExtractorChainException;




}
