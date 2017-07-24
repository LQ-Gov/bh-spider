package com.charles.spider.common.http;

import com.charles.spider.common.constant.HttpMethod;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-6-3.
 */
public class Request {
    private URL base;
    private HttpMethod method;

    private Map<String,String> headers = new HashMap<>();

    private Map<String,Object> params = new HashMap<>();

    private Map<String,Object> extra = new HashMap<>();

    private Map<String,String[]> extractors = new HashMap<>();


    @JsonCreator
    public Request(@JsonProperty("base") String url) throws MalformedURLException {
        this(url, HttpMethod.GET);
    }


    public Request(String url,HttpMethod method) throws MalformedURLException {
        this.base = new URL(url);
        this.method = method;


    }


    public URL url() {
        return base;
    }

    public HttpMethod method(){return method;}

    /**
     * 请求头设置
     * @return
     */
    public Map<String,String> headers(){
        return headers;
    }

    /**
     * 向服务器请求附带的参数
     * @return
     */
    public Map<String,Object> params(){return params;}


    /**
     * 框架消息传递附带的数据
     * @return
     */
    public Map<String,Object> extra(){return extra;}


    public String[] extractor(String key){
        return extractors.get(key);
    }

    public void extractor(String key,String[] modules){
        extractors.put(key,modules);
    }
}
