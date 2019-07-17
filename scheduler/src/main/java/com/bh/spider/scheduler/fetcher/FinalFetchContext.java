package com.bh.spider.scheduler.fetcher;


import com.bh.spider.common.fetch.Cookie;
import com.bh.spider.common.fetch.FetchContext;
import com.bh.spider.common.fetch.Response;
import com.bh.spider.doc.Document;
import com.bh.spider.doc.impl.DocumentImpl;
import com.bh.spider.scheduler.context.Context;

import java.nio.charset.Charset;
import java.util.List;

/**
 * FinalFetchContext包含了对response和document的操作
 */
public class FinalFetchContext extends BasicFetchContext {

    private Context context;
    private FetchContext parent;
    private Response response;




    public FinalFetchContext(FetchContext parent, Response response) {
        super(parent.request(),parent.rule(),parent.fields());

        this.parent = parent;
        this.response = response;

    }


    @Override
    public Response response() {
        return this.response;
    }


    @Override
    public Cookie cookie(String name) {
        return response.cookie(name);
    }

    @Override
    public List<Cookie> cookies() {
        return response.cookies();
    }


    @Override
    public Document document() {
        return new DocumentImpl(this.response.data());
    }

    @Override
    public Document document(Charset charset) {
        return new DocumentImpl(this.response.data(), charset);
    }










}
