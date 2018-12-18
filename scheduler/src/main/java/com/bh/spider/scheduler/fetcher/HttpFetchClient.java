package com.bh.spider.scheduler.fetcher;

import com.bh.spider.fetch.FetchContext;
import com.bh.spider.fetch.FetchContextUtils;
import com.bh.spider.fetch.Request;
import com.bh.spider.fetch.impl.FetchResponse;
import com.bh.spider.rule.Rule;
import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class HttpFetchClient implements FetchClient {
    private volatile static CloseableHttpAsyncClient client = null;



    private static CloseableHttpAsyncClient clientInstance() throws KeyManagementException, NoSuchAlgorithmException {
        if (client == null) {
            synchronized (HttpFetchClient.class) {
                if (client == null) {
                    client = HttpAsyncClientBuilder.create()
                            .setDefaultCookieStore(new HttpClientCookieStoreAdapter(CookieStoreFactory.get()))
                            .setSSLContext(SSLContextBuilder.create().build())
                            .setDefaultRequestConfig(RequestConfig.custom()
                                    .setCookieSpec(CookieSpecs.STANDARD)//解决invalid cookie header问题
                                    .build())
                            .build();




                    client.start();
                }
            }
        }
        return client;

    }

    @Override
    public CompletableFuture<FetchResponse> execute(FetchContext ctx) throws FetchExecuteException {
        try {
            Request req = ctx.request();
            HttpRequestBase base = toHttpRequest(req);

            final CompletableFuture<FetchResponse> future = new CompletableFuture<>();

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


                        List<HttpCookie> cookies = CookieStoreFactory.get().get(req.url().toURI());

                        FetchResponse fr = new FetchResponse(code, data, headerMap,
                                cookies.stream().map(FetchCookieAdapter::new).collect(Collectors.toList()));


                        future.complete(fr);

                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failed(Exception ex) {
                    future.completeExceptionally(ex);
                }

                @Override
                public void cancelled() {

                }
            });


            return future;
        } catch (URISyntaxException e) {
            throw new FetchExecuteException(e);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new FetchExecuteException("http client build failed", e);
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
