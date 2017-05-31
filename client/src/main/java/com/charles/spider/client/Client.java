package com.charles.spider.client;

import com.alibaba.fastjson.JSON;
import com.charles.common.spider.command.Commands;
import com.charles.common.task.Task;
import com.charles.common.task.TimerTask;
import com.charles.spider.common.moudle.Description;
import com.charles.spider.common.protocol.SerializeFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
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

    public void submit() {
        write(Commands.SUBMIT_TASK, null);
    }

    /**
     * 提交一次性任务
     * @param task
     */
    public void submit(Task task){write(Commands.SUBMIT_TASK,null);}

    /**
     * 提交定时任务（产生相同任务)
     * @param task
     */
    public void submit(TimerTask task){write(Commands.SUBMIT_TIMER,null);}

    /**
     * 提交定时任务(动态产生任务)
     * @param cron
     * @param path
     * @param cls
     */
    public void submit(String cron, Path path, String cls) throws IOException {

        write(Commands.SUBMIT_TIMER,null,Files.readAllBytes(path),cron,cls);
    }


    /**
     * 提交定时任务(动态产生任务)
     * @param cron
     * @param path
     * @param cls
     */
    public void submit(String cron,String path,String cls) throws IOException {
        submit(cron,Paths.get(path),cls);
    }

    /**
     * 提交module
     * @param path
     * @param desc
     */

    public void submit(Path path, Description desc,boolean override) throws IOException {
        byte[] data = Files.readAllBytes(path);

        write(Commands.SUBMIT_MODULE, null, data, desc,override);
    }





    /**
     * 提交module
     * @param path
     * @param desc
     */
    public void submit(String path,Description desc,boolean override) throws IOException {
        submit(Paths.get(path), desc,override);
    }



}
