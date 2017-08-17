package com.charles.spider.fetch.context;

import com.charles.spider.common.constant.HttpMethod;
import com.charles.spider.common.http.Request;
import com.charles.spider.query.annotation.GeneratedKey;
import com.charles.spider.query.annotation.Table;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Table("charles_spider_request")
public class FetchRequest implements Request {

    @GeneratedKey
    private String id;

    private URL base;
    private HttpMethod method;

    private Map<String, String> headers = new HashMap<>();

    private Map<String, Object> params = new HashMap<>();

    private Map<String, Object> extra = new HashMap<>();

    private Map<String, String[]> extractors = new HashMap<>();

    private FetchState state;
    private String message;
    private Date createTime;
    private Date updateTime;

    public FetchRequest(String url) throws MalformedURLException {
        this.base = new URL(url);
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


    public String[] extractor(String key) {
        return extractors.get(key);
    }

    public void extractor(String key, String[] modules) {
        extractors.put(key, modules);
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
}
