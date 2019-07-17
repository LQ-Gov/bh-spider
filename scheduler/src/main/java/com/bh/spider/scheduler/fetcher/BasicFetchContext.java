package com.bh.spider.scheduler.fetcher;

import com.bh.spider.common.fetch.Cookie;
import com.bh.spider.common.fetch.FetchContext;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.Response;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.doc.Document;
import com.bh.spider.scheduler.Scheduler;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实现了对于schedule和request的操作
 */
public class BasicFetchContext implements FetchContext {

    private Request original;
    private Scheduler sch;

    private Rule rule;

    private Map<String, Object> fields = new HashMap<>();

    public BasicFetchContext(Request original, Rule rule) {
        this(original,rule,null);



    }

    public BasicFetchContext(Request original, Rule rule,Map<String,Object> fields) {

        this.original = original;
        this.rule = rule;
        if (fields != null)
            this.fields.putAll(fields);

    }

    @Override
    public URL url() {
        return original.url();
    }

    @Override
    public Request request() {
        return this.original;
    }

    @Override
    public Response response() {
        return null;

    }

    @Override
    public Rule rule() {
        return rule;
    }

    @Override
    public Cookie cookie(String name) {
        return null;
    }

    @Override
    public List<Cookie> cookies() {
        return null;
    }

    @Override
    public Document document() {
        return null;
    }

    @Override
    public Document document(Charset charset) {
        return null;
    }


    @Override
    public void set(String key, Object value) {
        fields.put(key, value);

    }

    @Override
    public void set(Map<String, Object> collection) {
        fields.putAll(collection);

    }

    @Override
    public Object get(String key) {
        return fields.get(key);
    }

    @Override
    public Object get(String key, Object defaultValue) {
        return fields.getOrDefault(key,defaultValue);
    }

    @Override
    public Map<String, Object> fields() {
        return fields;
    }

    @Override
    public void schedule(FetchContext ctx, Request req, boolean local) throws Exception {
//        if (ctx != null) {
//            //格式化 req
//        }
//
//        sch.process(new Command(new LocalContext(sch), CommandCode.SUBMIT_REQUEST,req));

    }










     public void schedule(FetchContext ctx, List<Request> requests, boolean local){

//        sch.process(new Command(new LocalContext(sch), CommandCode.SUBMIT_REQUEST_BATCH,requests));
    }



}
