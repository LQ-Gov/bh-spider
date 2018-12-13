package com.bh.spider.fetch.impl;

import com.bh.spider.fetch.FetchMethod;
import com.bh.spider.fetch.Request;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.helper.HttpConnection;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RequestImpl implements Request {

    private long id;
    private FetchState state;
    private URL base;

    private FetchMethod method;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, Object> extra = new HashMap<>();

    private transient String hash;

    private long ruleId;

    private Date createTime;

    private RequestImpl() {
    }

    public RequestImpl(String url) throws MalformedURLException {
        this(url, FetchMethod.GET);
    }

    public RequestImpl(String url, FetchMethod method) throws MalformedURLException {
        this(0,url, method);
    }

    public RequestImpl(long id, String url, FetchMethod method) throws MalformedURLException {
        this(0, new URL(url), method);
    }

    public RequestImpl(URL url){
        this(url,FetchMethod.GET);
    }


    public RequestImpl(URL url, FetchMethod method) {
        this(0,url,method);
    }

    public RequestImpl(long id, URL url, FetchMethod method) {
        assert url != null;
        this.id=id;
        this.base = url;
        this.method = method == null ? FetchMethod.GET : method;
        this.createTime = new Date();
    }

    public long id() {
        return id;
    }
    public URL url() {
        return base;
    }

    public FetchMethod method() {
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

    @Override
    public long ruleId() {
        return ruleId;
    }

    public void setRuleId(long ruleId){
        this.ruleId = ruleId;
    }


    @Override
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
        return obj instanceof RequestImpl && url().equals(((RequestImpl) obj).url());

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        RequestImpl cloneObject = (RequestImpl) super.clone();

        return cloneObject;
    }

    public FetchState state() {
        return state;
    }

    void setState(FetchState state) {
        this.state = state;
    }


}
