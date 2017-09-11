package com.bh.spider.fetch.impl;

import com.bh.spider.fetch.HttpMethod;
import com.bh.spider.fetch.Request;
import com.bh.spider.transfer.entity.Rule;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class FetchRequest implements Request {

    private long id;

    private URL base;
    private HttpMethod method;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, Object> params = new HashMap<>();

    private Map<String, Object> extra = new HashMap<>();

    private Rule rule;
    private String h;
    private FetchState state;
    private String message;
    private Date createTime;
    private Date updateTime;

    public FetchRequest() {
    }

    public FetchRequest(String url) throws MalformedURLException {
        this(url, HttpMethod.GET);
    }

    public FetchRequest(String url, HttpMethod method) throws MalformedURLException {
        this(new URL(url), method);
    }


    public FetchRequest(URL url, HttpMethod method) {
        this.base = url;
        this.method = method;
        this.h = DigestUtils.sha1Hex(url.toString());
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public URL url() {
        return base;
    }

    public HttpMethod method() {
        return method;
    }

    /**
     * 请求头设置
     *
     * @return
     */
    public Map<String, String> headers() {
        return headers;
    }

    /**
     * 向服务器请求附带的参数
     *
     * @return
     */
    public Map<String, Object> params() {
        return params;
    }


    /**
     * 框架消息传递附带的数据
     *
     * @return
     */
    public Map<String, Object> extra() {
        return extra;
    }

    @Override
    public String[] extractor(String key) {
        return this.rule == null ? null : this.rule.extractor(key);
    }

    @Override
    public void extractor(String key, String[] modules) {

         this.rule.extractor(key, modules);
    }


    public FetchState getState() {
        return state;
    }

    public void setState(FetchState state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String hash() {
        return h;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
