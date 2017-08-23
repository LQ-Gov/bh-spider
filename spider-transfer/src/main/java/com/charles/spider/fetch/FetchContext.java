package com.charles.spider.fetch;

import com.charles.spider.doc.Document;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

public interface FetchContext {


    URL url();

    Request request();

    Response response();

    Document document();

    Document document(Charset charset);

    void set(String key, Object value);

    void set(Map<String, Object> collection);

    Object get(String key);


    void scheduler(FetchContext ctx, Request req, boolean local) throws Exception;

    void scheduler(FetchContext ctx, Request req) throws Exception;

    void scheduler(Request req) throws Exception;


    void scheduler(FetchContext ctx, String url, boolean local) throws Exception;

    void scheduler(FetchContext ctx, String url) throws Exception;

    void scheduler(String url) throws Exception;

    void cancel() throws ExtractorChainException;


}
