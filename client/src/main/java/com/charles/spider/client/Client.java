package com.charles.spider.client;

import com.alibaba.fastjson.JSON;
import com.charles.common.spider.command.Commands;
import com.charles.common.task.Task;
import com.charles.common.task.TimerTask;
import com.charles.spider.common.moudle.Description;
import com.charles.spider.common.moudle.ModuleType;
import com.charles.spider.common.protocol.SerializeFactory;
import com.charles.spider.store.base.Store;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
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

    public Client(String servers) throws IOException, URISyntaxException {
        this.servers = Arrays.asList(servers.split(","));
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

    protected <T> T write(Commands cmd, Class<T> cls, T... params) {
        short type = (short) cmd.ordinal();
        try {
            byte[] data = SerializeFactory.serialize(params);
            DataOutputStream out = new DataOutputStream(socket.get().getOutputStream());
            out.writeShort(type);
            out.writeInt(data.length);
            out.write(data);
            out.flush();

            DataInputStream in = new DataInputStream(socket.get().getInputStream());

            boolean quit;

//            T o = null;
//            while (socket.get().isConnected()) {
//                quit = !in.readBoolean();
//                if (quit) break;
//                byte[] result = new byte[in.readInt()];
//                if (result.length <= 0) continue;
//
//                in.readFully(result);
//                if (cls == null)
//                    System.out.println(new String(result));
//                else
//                    o = JSON.parseObject(result, cls);
//            }
//            return o;
            return null;
        } catch (IOException e) {
            e.fillInStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public void submit(Class<?> cls) {
        submit(cls, false);
    }

    public void submit(Class<?> cls, boolean override) {
        submit(cls,override);
    }

    public void submit(Class<?> cls, Description desc, boolean override) throws IOException {
        URL url = cls.getResource("");
        submit(url.getPath(), desc, override);
    }


    public void submit(String path) {
        submit(path, false);
    }

    public void submit(String path, boolean override) {
        submit(Paths.get(path), override);
    }


    public void submit(Path path) {
        submit(path, false);
    }

    public void submit(Path path, boolean override) {
    }

    /**
     * 提交module
     *
     * @param path
     * @param desc
     */
    public void submit(String path, Description desc, boolean override) throws IOException {
        submit(Paths.get(path), desc, override);
    }

    /**
     * 提交module
     *
     * @param path
     * @param desc
     */

    public void submit(Path path, Description desc, boolean override) throws IOException {
        assert desc != null;

        Preconditions.checkNotNull(desc, "the parameter of desc can't null");
        Preconditions.checkArgument(Files.exists(path), "the file isn't exist");


        if (StringUtils.isBlank(desc.getName()))
            desc.setName(path.getFileName().toString());

        if (desc.getType() == ModuleType.UNKNOWN) {
            int index = desc.getName().lastIndexOf('.');
            Preconditions.checkArgument(index > 0 && index < desc.getName().length(),
                    "can't analysis module type");

            String type = desc.getName().substring(index);
            desc.setType(ModuleType.valueOf(type));
        }


        byte[] data = Files.readAllBytes(path);


        write(Commands.SUBMIT_MODULE, null, data, desc, override);
    }


    public List<Description> modules(int skip,int size){
        return null;
    }


    public Store store(){
        return null;
    }



}
