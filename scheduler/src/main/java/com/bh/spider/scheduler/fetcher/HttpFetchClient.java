package com.bh.spider.scheduler.fetcher;

import com.bh.common.utils.ArrayUtils;
import com.bh.spider.common.fetch.FetchContext;
import com.bh.spider.common.fetch.FetchContextUtils;
import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.impl.FetchResponse;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.scheduler.Config;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpFetchClient implements FetchClient {
    private volatile static CloseableHttpClient client = null;

    private final int maxConnection;

    public HttpFetchClient(Config config) {

        maxConnection = Integer.valueOf(config.get(Config.INIT_PROCESSOR_THREADS_COUNT));

    }


    private CloseableHttpClient clientInstance() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
        if (client == null) {
            synchronized (HttpFetchClient.class) {
                if (client == null) {

                    SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, (TrustStrategy) (x509Certificates, s) -> true).build();
                    SSLConnectionSocketFactory ssl = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);


                    Registry<ConnectionSocketFactory> socketFactoryRegistry =
                            RegistryBuilder.<ConnectionSocketFactory>create()
                                    .register("https", ssl)
                                    .register("http", new PlainConnectionSocketFactory())
                                    .build();


                    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                    connectionManager.setMaxTotal(maxConnection);

                    client = HttpClientBuilder.create()
                            .setSSLSocketFactory(ssl)
                            .setConnectionManager(connectionManager)
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

            config(base, ctx.rule());


            try(CloseableHttpResponse response = clientInstance().execute(base)) {
                int code = response.getStatusLine().getStatusCode();

                Header contentEncoding = response.getEntity().getContentEncoding();

                if (contentEncoding != null) {
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
            }

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
    private void config(HttpRequestBase base, Rule rule) {


        if (rule == null) return;

        RequestConfig.Builder builder = RequestConfig.custom()
                .setConnectTimeout(rule.getTimeout())
                .setSocketTimeout(rule.getTimeout());


        if (ArrayUtils.isNotEmpty(rule.getProxies())) {
            //随机选择一个代理
            int index = RandomUtils.nextInt(0, rule.getProxies().length);
            URI uri = URI.create("proxy://" + rule.getProxies()[index]);

            HttpHost proxy = new HttpHost(uri.getHost(), uri.getPort(), base.getURI().getScheme());

            builder.setProxy(proxy);
        }

        base.setConfig(builder.build());

    }
}
