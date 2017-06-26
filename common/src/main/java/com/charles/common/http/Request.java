package com.charles.common.http;

import com.charles.common.HttpMethod;

import java.net.URI;
import java.util.Map;

/**
 * Created by lq on 17-6-3.
 */
public interface Request {

    URI uri();

    HttpMethod method();

    Map<String,String> headers();

    Map<String,Object> params();

    Map<String,Object> extra();
}
