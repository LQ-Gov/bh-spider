package com.bh.spider.client;

import com.bh.common.utils.CommandCode;
import com.bh.common.utils.Json;
import com.bh.common.watch.WatchEvent;
import com.bh.spider.client.context.ClientFetchContext;
import com.bh.spider.client.converter.TypeConverter;
import com.bh.spider.client.watch.WatchOperation;
import com.bh.spider.common.fetch.*;
import com.bh.spider.common.fetch.impl.FetchResponse;
import com.bh.spider.common.fetch.impl.RequestBuilder;
import com.bh.spider.common.member.Node;
import com.bh.spider.common.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Created by lq on 17-3-25.
 */
public class Client {
    private final static Logger logger = LoggerFactory.getLogger(Client.class);


    private String server = null;

    private RuleOperation ruleOperation = null;
    private ComponentOperation componentOperation = null;
    private RequestOperation requestOperation = null;

    private WatchOperation watchOperation = null;


    private Properties properties = null;

    private List<InetSocketAddress> addresses;

    private Communicator communicator = new Communicator();

    public Client(String server) {
        this(server, null);
    }


    public Client(String server, Properties properties) {
        this.properties = properties == null ? new Properties() : properties;
        this.server = server;

        this.addresses = convertToSocketAddress(server);

        this.ruleOperation = new RuleOperation(this.communicator);
        this.componentOperation = new ComponentOperation(this.communicator, this.properties);
        this.requestOperation = new RequestOperation(this.communicator);
        this.watchOperation = new WatchOperation(this.communicator);

    }


    public void connect() {

        communicator.connect(this.addresses,true);


        try {

            String addrs = communicator.write(CommandCode.SYNC_SERVER_LIST, String.class);
             convertToSocketAddress(addrs);
        }catch (Exception e){}

    }



    private List<InetSocketAddress> convertToSocketAddress(String str) {

        List<InetSocketAddress> addresses = new LinkedList<>();

        String[] servers = str.split(",");

        for (String server : servers) {
            try {
                URI uri = new URI("tcp://" + server);
                InetSocketAddress address = new InetSocketAddress(uri.getHost(), uri.getPort());
                addresses.add(address);
            } catch (Exception e) {
                logger.info(null, e);
            }
        }

        return addresses;


    }


    public ComponentOperation component() {
        return componentOperation;
    }

    public RuleOperation rule() {
        return ruleOperation;
    }

    public RequestOperation request() {
        return requestOperation;
    }




    @SafeVarargs
    public final Future<FetchResponse> crawler(Request req, Rule rule, Class<? extends Extractor>... extractors) throws MalformedURLException {

        return communicator.stream(CommandCode.FETCH, response -> {


            FetchContext ctx = new ClientFetchContext(req, response);
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

    public <T> void watch(String point, Consumer<T> consumer, Class<T> valueClass) {

        communicator.stream(CommandCode.WATCH, consumer, new TypeConverter<>(valueClass), point);
    }

    public boolean watch(String point, Consumer<WatchEvent> consumer) throws Exception {
        this.watchOperation.watch(point, consumer, WatchEvent.class);

        return true;
    }

    public void unwatch(String point) {
        this.watchOperation.unwatch(point);
    }


    public void unwatch(String point, Consumer<?> consumer) {
        this.watchOperation.unwatch(point, consumer);
    }


    public Map<String, String> profile() {
        Type returnType = Json.mapType(String.class, String.class);
        return communicator.write(CommandCode.PROFILE, returnType);
    }


    public List<Node> nodes() {
        ParameterizedType returnType = ParameterizedTypeImpl.make(List.class, new Type[]{Node.class}, null);
        return communicator.write(CommandCode.GET_NODE_LIST, returnType);
    }




}
