package com.charles.spider.client;

import com.charles.common.JsonFactory;
import com.charles.spider.client.converter.StringConverter;
import com.charles.spider.client.converter.TypeConverter;
import com.charles.spider.client.receiver.Receiver;
import com.charles.spider.common.command.Commands;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
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
    public final static byte DISPOSABLE_REQUEST = 0;
    public final static byte STREAM_REQUEST = 1;


    private final static ObjectMapper mapper = JsonFactory.get();

    private String server = null;
    private Socket socket = null;
    private DataOutputStream out = null;

    private RuleOperation ruleOperation = null;
    private ModuleOperation moduleOperation = null;
    private RequestOperation requestOperation = null;

    private Receiver receiver = null;
    private final AtomicLong ID = new AtomicLong(0);


    public Client(String server) throws IOException, URISyntaxException {
        this.server = server;


        if (open()) {
            ruleOperation = new RuleOperation(this);
            moduleOperation = new ModuleOperation(this);
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

    private synchronized long write0(long id, Commands cmd, byte flag, Object... params) throws IOException {
        short type = (short) cmd.ordinal();
        byte[] data = params == null || params.length == 0 ? new byte[0] : mapper.writeValueAsBytes(params);

        out.writeShort(type);
        out.writeLong(id);
        out.writeByte(flag);
        out.writeInt(data.length);
        out.write(data);
        out.flush();

        return id;

    }


    protected <T> T write(Commands cmd, Type t, Object... params) {


        try {
            long id = ID.incrementAndGet();
            Future<T> future = receiver.watch(id, new TypeConverter<>(t));

            write0(id, cmd, DISPOSABLE_REQUEST, params);

            return future.get();
        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected void stream(Commands cmd, Consumer<String> consumer, Object... params) {
        try {
            long id = ID.incrementAndGet();
            receiver.watch(id, consumer, new StringConverter());
            write0(id, cmd, STREAM_REQUEST, params);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public ModuleOperation module() {
        return moduleOperation;
    }

    public RuleOperation rule() {
        return ruleOperation;
    }

    public RequestOperation request() {
        return requestOperation;
    }


    public boolean crawler(String url, Class<?>... extractors) {
        return false;
    }

    public void watch(String point, Consumer<String> consumer) throws IOException {
        stream(Commands.WATCH, consumer, point);
    }


}
