package com.charles.spider.client;

import com.charles.common.JsonFactory;
import com.charles.spider.common.command.Commands;
import com.charles.spider.common.protocol.SerializeFactory;
import com.charles.spider.common.protocol.jackson.JacksonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lq on 17-3-25.
 */
public class Client {

    private final static ObjectMapper mapper = JsonFactory.get();

    private String server = null;
    private Socket socket = null;

    private RuleOperation ruleOperation = null;
    private ModuleOperation moduleOperation = null;
    private RequestOperation requestOperation = null;


    public Client(String server) throws IOException, URISyntaxException {
        this.server = server;
        moduleOperation = new ModuleOperation(this);
        ruleOperation = new RuleOperation(this);
        requestOperation = new RequestOperation(this);
        open();
    }


    private boolean open() throws URISyntaxException, IOException {
        URI uri = new URI("tcp://" + server);
        socket = new Socket(uri.getHost(), uri.getPort());
        return true;
    }

    public void close() throws IOException, InterruptedException {
        if (socket != null && socket.isConnected()) socket.close();

    }

    protected synchronized <T> T write(Commands cmd, Type cls, Object... params) {
        short type = (short) cmd.ordinal();
        try {
            byte[] data = params == null || params.length == 0 ? new byte[0] : mapper.writeValueAsBytes(params);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeShort(type);
            out.writeInt(data.length);
            out.write(data);
            out.flush();

            DataInputStream in = new DataInputStream(socket.getInputStream());

            boolean complete = in.readBoolean();

            int len = in.readInt();

            if (len == 0 && complete) return null;

            data = new byte[len];

            in.readFully(data);

            return cls == null ? null : mapper.readValue(data, mapper.getTypeFactory().constructType(cls));


        } catch (IOException e) {
            e.fillInStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void stream(){

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


    public boolean crawler(String url,Class<?>... extractors){
        return false;
    }





}
