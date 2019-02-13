package com.bh.spider.client.sender;

import com.bh.spider.client.converter.Converter;
import com.bh.spider.client.converter.TypeConverter;
import com.bh.spider.client.receiver.Receiver;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Conversion;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class Sender {

    private final static ObjectMapper mapper = Json.get();

    private final AtomicLong ID = new AtomicLong(0);

    private DataOutputStream out;

    private Receiver receiver;

    private HeartBeat heartBeat;

    private long lastWriteTime;




    public Sender(Socket socket, Receiver receiver) throws IOException {
        this.out = new DataOutputStream(socket.getOutputStream());
        this.receiver = receiver;

        this.heartBeat = new HeartBeat(this, 10000);
//        this.heartBeat.start();

    }

    /**
     * 最终发送方法
     * @param id command id
     * @param cmd command code
     * @param params command params
     * @throws IOException
     */
    private synchronized void write0(long id, CommandCode cmd, Object... params) throws IOException {
        short cmdCode = (short) cmd.ordinal();
        byte[] data = params == null || params.length == 0 ? new byte[0] : mapper.writeValueAsBytes(params);

        byte[] result = new byte[8 + 2 + 4 + data.length];
        Conversion.longToByteArray(id, 0, result, 0, Long.BYTES);

        Conversion.shortToByteArray(cmdCode, 0, result, 8, Short.BYTES);

        Conversion.intToByteArray(data.length, 0, result, 10, Integer.BYTES);

        System.arraycopy(data, 0, result, 14, data.length);

        out.write(data);
        out.flush();

        lastWriteTime = System.currentTimeMillis();
    }


    public void write(CommandCode cmd) throws IOException {
        long id = ID.incrementAndGet();
        write0(id, cmd);
    }


    public  <T> T write(CommandCode cmd, Type t, Object... params) {
        try {
            Future<T> future = stream(cmd, null, new TypeConverter<>(t), params);

            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    public  <T> Future<T> stream(CommandCode cmd, Consumer<T> consumer, Converter<byte[], T> converter, Object... params) {
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


    public long lastWriteTime(){
        return lastWriteTime;
    }


    public void close() throws InterruptedException {
        this.heartBeat.close();
    }
}
