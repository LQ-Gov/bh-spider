package com.charles.spider.fetch.context;

import com.charles.spider.common.http.Response;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FetchResponse implements Response {
    private int code;
    private byte[] entity;
    private Map<String,String> headers = new HashMap<>();

    public FetchResponse(HttpResponse response) throws IOException {
        this.code = response.getStatusLine().getStatusCode();
        this.entity = EntityUtils.toByteArray(response.getEntity());
        Header[] list = response.getAllHeaders();
        for(Header h:list){
            headers.put(h.getName(),h.getValue());
        }

    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public Map<String, String> headers() {
        return headers;
    }

    @Override
    public byte[] data() {
        return entity;
    }
}
