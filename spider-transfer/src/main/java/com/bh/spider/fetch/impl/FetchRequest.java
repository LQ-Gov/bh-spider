package com.bh.spider.fetch.impl;

import com.bh.spider.fetch.HttpMethod;
import com.bh.spider.fetch.Request;
import org.apache.commons.codec.digest.DigestUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FetchRequest implements Request {

    private long id;
    private FetchState state;
    private URL base;
    private HttpMethod method;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, Object> extra = new HashMap<>();

    private transient String hash;
    private Date createTime;

    private FetchRequest() {
    }

    FetchRequest(String url) throws MalformedURLException {
        this(url, HttpMethod.GET);
    }

    FetchRequest(String url, HttpMethod method) throws MalformedURLException {
        this(new URL(url), method);
    }


    FetchRequest(URL url, HttpMethod method) {
        assert url != null;
        this.base = url;
        this.method = method == null ? HttpMethod.GET : method;
        this.createTime = new Date();
    }

    public long id() {
        return id;
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
     * 框架消息传递附带的数据
     *
     * @return
     */
    public Map<String, Object> extra() {
        return extra;
    }


    public String hash() {
        String h = hash;
        if (h == null) {
            h = DigestUtils.sha1Hex(url().toString());
            hash = h;
        }
        return h;
    }

    public Date createTime() {
        return createTime;
    }

    void setCreateTime(Date date) {
        this.createTime = date;
    }

    @Override
    public int hashCode() {
        return url().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FetchRequest && url().equals(((FetchRequest) obj).url());

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FetchRequest cloneObject = (FetchRequest) super.clone();

        return cloneObject;
    }

    public FetchState state() {
        return state;
    }

    void setState(FetchState state) {
        this.state = state;
    }


}
