package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchRequest;
import com.bh.spider.fetch.impl.FetchResponse;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie2;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.CookieManager;
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
    private volatile static CloseableHttpAsyncClient client = null;


    static {


    }

    private static CloseableHttpAsyncClient clientInstance() throws KeyManagementException, NoSuchAlgorithmException {
        if (client == null) {
            synchronized (HttpFetchClient.class) {
                if (client == null) {
                    client = HttpAsyncClientBuilder.create()
                            .setDefaultCookieStore(new HttpClientCookieStoreAdapter(CookieStoreFactory.get()))
                            .setSSLContext(SSLContextBuilder.create().build())
                            .build();


                    client.start();
                }
            }
        }
        return client;

    }

    @Override
    public void execute(FetchRequest request, FutureCallback<FetchResponse> callback) throws FetchExecuteException {
        try {
            HttpRequestBase base = toHttpRequest(request);



            clientInstance().execute(base, new FutureCallback<HttpResponse>() {
                @Override
                public void completed(HttpResponse result) {
                    try {
                        int code = result.getStatusLine().getStatusCode();
                        HeaderElement[] headerElements = result.getEntity().getContentEncoding().getElements();

                        for (HeaderElement element : headerElements) {
                            if (element.getName().equalsIgnoreCase("gzip")) {
                                result.setEntity(new GzipDecompressingEntity(result.getEntity()));
                                break;
                            }
                        }

                        byte[] data = EntityUtils.toByteArray(result.getEntity());

                        Header[] headers = result.getAllHeaders();
                        Map<String, String> headerMap = new HashMap<>();

                        Arrays.stream(headers).forEach(x -> headerMap.put(x.getName(), x.getValue()));


                        List<HttpCookie> cookies = CookieStoreFactory.get().get(request.url().toURI());

                        FetchResponse fr = new FetchResponse(code, data, headerMap,
                                cookies.stream().map(FetchCookieAdapter::new).collect(Collectors.toList()));
                        callback.completed(fr);
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(Exception ex) {
                    callback.failed(ex);
                }

                @Override
                public void cancelled() {
                    callback.cancelled();
                }
            });
        } catch (URISyntaxException e) {
            throw new FetchExecuteException(e);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new FetchExecuteException("http client build failed",e);
        }


    }


    protected HttpRequestBase toHttpRequest(Request original) throws URISyntaxException {
        HttpRequestBase base;

        URI uri = original.url().toURI();

        switch (original.method()) {
            case GET:
                base = new HttpGet(uri);
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
