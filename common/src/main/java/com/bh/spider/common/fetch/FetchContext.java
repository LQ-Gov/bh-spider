package com.bh.spider.common.fetch;

import com.bh.common.utils.URLUtils;
import com.bh.spider.common.fetch.impl.RequestBuilder;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.doc.Document;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
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

    Object get(String key, Object defaultValue);

    Map<String, Object> fields();


    void schedule(FetchContext ctx, Request req, boolean local) throws Exception;


    void schedule(FetchContext ctx, List<Request> requests, boolean local) throws Exception;


    default void schedule(FetchContext ctx, Request req) throws Exception {
        schedule(ctx, req, false);
    }


    default void schedule(Request req) throws Exception {
        schedule(null, req);
    }

    default void schedule(FetchContext ctx, String url, boolean local) throws Exception {
        schedule(ctx, RequestBuilder.create(url).build(), local);
    }


    default void schedule(FetchContext ctx, String url) throws Exception {
        schedule(ctx, url, false);
    }

    default void schedule(String url) throws Exception {
        schedule(null, url);
    }


    default void schedule(FetchContext ctx, List<String> requests) throws Exception {
        String protocol = this.url().getProtocol();
        String host = this.url().getHost();

        List<Request> list = new LinkedList<>();
        for (String request : requests) {
            request = URLUtils.format(request, protocol, host);

            list.add(RequestBuilder.create(request).build());
        }

        schedule(ctx, list, false);

    }

    default void schedule(List<String> requests) throws Exception {
        schedule(null, requests);

    }

    default void termination() throws ExtractorChainException {
        throw new ExtractorChainException(Behaviour.TERMINATION);
    }


}
