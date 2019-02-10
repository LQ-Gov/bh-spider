package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.FetchContext;
import com.bh.spider.fetch.FetchContextUtils;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchResponse;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpFetchClient implements FetchClient {
    private volatile static HttpClient client = null;



    private static HttpClient clientInstance() throws KeyManagementException, NoSuchAlgorithmException {
        if (client == null) {
            synchronized (HttpFetchClient.class) {
                if (client == null) {
                    client = HttpClientBuilder.create()
                            .setDefaultCookieStore(new HttpClientCookieStoreAdapter(CookieStoreFactory.get()))
                            .setSSLContext(SSLContextBuilder.create().build())
                            .setDefaultRequestConfig(RequestConfig.custom()
                                    .setCookieSpec(CookieSpecs.STANDARD)//解决invalid cookie header问题
                                    .build())
                            .build();
                }
            }
        }
        return client;

    }

    @Override
    public FetchResponse execute(FetchContext ctx) throws FetchExecuteException {

        try {
            Request req = ctx.request();
            HttpRequestBase base = toHttpRequest(req);
            HttpResponse response = clientInstance().execute(base);
            int code = response.getStatusLine().getStatusCode();

            Header contentEncoding = response.getEntity().getContentEncoding();

            if(contentEncoding!=null) {
                for (HeaderElement element : contentEncoding.getElements()) {
                    if (element.getName().equalsIgnoreCase("gzip")) {
                        response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                        break;
                    }
                }
            }
            byte[] data = EntityUtils.toByteArray(response.getEntity());

            Header[] headers = response.getAllHeaders();
            Map<String, String> headerMap = new HashMap<>();
            Arrays.stream(headers).forEach(x -> headerMap.put(x.getName(), x.getValue()));


            List<HttpCookie> cookies = CookieStoreFactory.get().get(req.url().toURI());

            FetchResponse wrapper = new FetchResponse(code, data, headerMap,
                    cookies.stream().map(FetchCookieAdapter::new).collect(Collectors.toList()));

            return wrapper;

        } catch (Exception e) {
            throw new FetchExecuteException(e);
        }
    }


    protected HttpRequestBase toHttpRequest(Request original) throws URISyntaxException {
        HttpRequestBase base;

        URI uri = original.url().toURI();

        switch (original.method()) {
            case GET: {
                uri = FetchContextUtils.instance().toURL(original).toURI();
                base = new HttpGet(uri);
            }
            break;
            case POST:
                base = new HttpPost(uri);
                break;
            case PUT:
                base = new HttpPut(uri);
                break;
            case HEAD:
                base = new HttpHead(uri);
                break;
            case PATCH:
                base = new HttpPatch(uri);
                break;

            case TRACE:
                base = new HttpTrace(uri);
                break;

            case DELETE:
                base = new HttpDelete(uri);
                break;
            case OPTIONS:
                base = new HttpOptions(uri);
                break;

            default:
                throw new RuntimeException("not support this type");
        }
        original.headers().forEach(base::setHeader);

        return base;
    }
}
