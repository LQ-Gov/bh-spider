package com.charles.spider.client;

import com.charles.common.spider.command.Commands;
import com.charles.spider.common.moudle.Description;
import com.charles.spider.common.moudle.ModuleType;
import com.charles.spider.common.protocol.SerializeFactory;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lq on 17-3-25.
 */
public class Client {

    private List<String> servers = null;
    private ThreadLocal<Integer> c = new ThreadLocal<>();
    private ThreadLocal<Socket> socket = new ThreadLocal<>();

    private ModuleOperation moduleOperation = null;
    private RuleOperation ruleOperation = null;
    private RequestOperation requestOperation = null;


    public Client(String servers) throws IOException, URISyntaxException {
        this.servers = Arrays.asList(servers.split(","));

        //moduleOperation = new

        open(next());
    }

    private int next() {
        Integer n = c.get();
        if (n == null)
            c.set(n = -1);
        c.set(n = ((n + 1) % servers.size()));
        return n;
    }

    private boolean open(int i) throws URISyntaxException, IOException {
        if (i >= servers.size()) return false;
        URI uri = new URI("tcp://" + servers.get(i));
        socket.set(new Socket(uri.getHost(), uri.getPort()));
        return true;
    }

    public void close() throws IOException, InterruptedException {
        if (socket != null && socket.get().isConnected()) socket.get().close();

    }

    protected <T> T write(Commands cmd, Type cls, Object... params) {
        short type = (short) cmd.ordinal();
        try {
            byte[] data = SerializeFactory.serialize(params);
            DataOutputStream out = new DataOutputStream(socket.get().getOutputStream());
            out.writeShort(type);
            out.writeInt(data.length);
            out.write(data);
            out.flush();

            DataInputStream in = new DataInputStream(socket.get().getInputStream());

            boolean complete = in.readBoolean();

            int len = in.readInt();

            if (len == 0 && complete) return null;

            data = new byte[len];

            in.readFully(data);

            //
            //return SerializeFactory.deserialize(data,cls);
            return null;

        } catch (IOException e) {
            e.fillInStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public ModuleOperation module() {return moduleOperation;}

    public RuleOperation rule() {return ruleOperation;}

    public RequestOperation request(){return requestOperation;}







}
