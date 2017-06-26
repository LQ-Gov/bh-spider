package com.charles.common.http;

import com.charles.common.HttpMethod;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-6-3.
 */
public class HttpRequest implements Request {

    private URI uri = null;
    private HttpMethod method = HttpMethod.GET;
    private Map<String,String> headers = new HashMap<>();
    private Map<String,Object> params = new HashMap<>();
    private Map<String,Object> extra = new HashMap<>();



    public HttpRequest(String url) throws URISyntaxException {
        this(url,HttpMethod.GET);
    }


    public HttpRequest(String url, HttpMethod method) throws URISyntaxException {
        this(new URI(url),method,null,null);
    }

    public HttpRequest(String url,HttpMethod method,Map<String,String> headers,Map<String,Object> params) throws URISyntaxException {
        this(new URI(url),method,headers,params);
    }

    public HttpRequest(URI uri,HttpMethod method,Map<String,String> headers,Map<String,Object> params){
        this(uri,method,headers,params,null);
    }

    public HttpRequest(URI uri,HttpMethod method,Map<String,String> headers,Map<String,Object> params,Map<String,Object> extra){
        this.uri = uri;
        this.method = method;
        this.headers =headers;
        this.params = params;
        this.extra = extra;
    }

    @Override
    public URI uri() {
        return this.uri;
    }

    @Override
    public HttpMethod method() {
        return method;
    }

    @Override
    public Map<String, String> headers() {
        return headers;
    }

    @Override
    public Map<String, Object> params() {
        return params;
    }

    @Override
    public Map<String, Object> extra() {
        return extra;
    }
}
