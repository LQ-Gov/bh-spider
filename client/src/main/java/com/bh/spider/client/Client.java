package com.bh.spider.client;

import com.bh.spider.client.context.ClientFetchContext;
import com.bh.spider.client.converter.Converter;
import com.bh.spider.client.converter.StringConverter;
import com.bh.spider.client.converter.TypeConverter;
import com.bh.spider.client.receiver.Receiver;
import com.bh.spider.fetch.*;
import com.bh.spider.fetch.impl.RequestBuilder;
import com.bh.spider.fetch.impl.FetchResponse;
import com.bh.spider.fetch.impl.FinalFetchContext;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.JsonFactory;
import com.bh.spider.transfer.entity.Rule;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Created by lq on 17-3-25.
 */
public class Client {

    private final static ObjectMapper mapper = JsonFactory.get();

    private String server = null;
    private Socket socket = null;
    private DataOutputStream out = null;

    private RuleOperation ruleOperation = null;
    private ComponentOperation moduleOperation = null;
    private RequestOperation requestOperation = null;

    private Receiver receiver = null;
    private final AtomicLong ID = new AtomicLong(0);


    public Client(String server) throws IOException, URISyntaxException {
        this.server = server;


        if (open()) {
            ruleOperation = new RuleOperation(this);
            moduleOperation = new ComponentOperation(this);
            requestOperation = new RequestOperation(this);
            receiver = new Receiver(socket);
            receiver.start();
        }
    }


    private boolean open() throws URISyntaxException, IOException {
        URI uri = new URI("tcp://" + server);
        socket = new Socket(uri.getHost(), uri.getPort());
        out = new DataOutputStream(socket.getOutputStream());
        return true;
    }

    public void close() throws IOException, InterruptedException {
        if (socket != null && socket.isConnected()) socket.close();
        if (receiver.isAlive()) receiver.join();

    }

    private synchronized void write0(long id, CommandCode cmd, Object... params) throws IOException {
        short type = (short) cmd.ordinal();
        byte[] data = params == null || params.length == 0 ? new byte[0] : mapper.writeValueAsBytes(params);

        out.writeShort(type);
        out.writeLong(id);
        out.writeInt(data.length);
        out.write(data);
        out.flush();
    }


    protected <T> T write(CommandCode cmd, Type t, Object... params) {
        try {
            Future<T> future = stream(cmd, null, new TypeConverter<>(t), params);

            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected <T> Future<T> stream(CommandCode cmd, Consumer<T> consumer, Converter<byte[], T> converter, Object... params) {
        try {
            long id = ID.incrementAndGet();
            Future<T> future = receiver.watch(id, consumer, converter);
            write0(id, cmd, params);

            return future;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ComponentOperation component() {
        return moduleOperation;
    }

    public RuleOperation rule() {
        return ruleOperation;
    }

    public RequestOperation request() {
        return requestOperation;
    }


    @SafeVarargs
    public final Future<FetchResponse> crawler(Request req, Rule rule, Class<? extends Extractor>... extractors) throws MalformedURLException {

        FetchContext base = new ClientFetchContext(req);
        return stream(CommandCode.FETCH, response -> {

            FetchContext ctx = new FinalFetchContext(base, response);
            try {
                for (Class<?> it : extractors) {

                    Extractor extractor = (Extractor) it.newInstance();
                    try {
                        extractor.run(ctx);
                    } catch (ExtractorChainException e) {
                        if (e.result() == Behaviour.TERMINATION) break;

                    } catch (Exception e) {
                        //此处做报告
                        e.printStackTrace();
                    }

                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }


        }, new TypeConverter<>(FetchResponse.class), req, rule);
    }

    @SafeVarargs
    public final Future<FetchResponse> crawler(Request req, Class<? extends Extractor>... extractors) throws MalformedURLException {
        return crawler(req, null, extractors);
    }


    @SafeVarargs
    public final Future<FetchResponse> crawler(String url, Class<? extends Extractor>... extractors) throws MalformedURLException {
        return crawler(url, null, extractors);
    }

    @SafeVarargs
    public final Future<FetchResponse> crawler(String url, Rule rule, Class<? extends Extractor>... extractors) throws MalformedURLException {
        return crawler(RequestBuilder.create(url).build(), rule, extractors);
    }

    public void watch(String point, Consumer<String> consumer) throws IOException {
        stream(CommandCode.WATCH, consumer, new StringConverter(), point);
    }


}
