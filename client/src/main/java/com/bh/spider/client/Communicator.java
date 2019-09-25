package com.bh.spider.client;

import com.bh.common.utils.CommandCode;
import com.bh.spider.client.converter.Converter;
import com.bh.spider.client.converter.TypeConverter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author liuqi19
 * @version Communicator, 2019-07-30 18:29 liuqi19
 **/
public class Communicator {

    private final AtomicLong ID = new AtomicLong(0);


    private ClientConnection[] connections = new ClientConnection[0];

    private int connectionIndex = 0;


    private Receiver receiver;


    public Communicator() {
        this.receiver = new Receiver();
    }

    public synchronized void connect(List<InetSocketAddress> addresses, boolean reconnect) {


        ClientConnection[] connections = new ClientConnection[addresses.size()];

        int pos = 0;
        for (InetSocketAddress address : addresses) {
            ClientConnection connection = null;
            if (!reconnect) {
                connection = searchInConnections(address);
            }

            if (connection == null) {
                connection = connect0(address, this.receiver);
            }
            connections[pos++] = connection;
        }
        this.connections = connections;
        sync();


    }

    private ClientConnection connect0(InetSocketAddress address, Receiver receiver) {
        ClientConnection connection = new ClientConnection(address);
        connection.connect(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(io.netty.channel.socket.SocketChannel ch) {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 9, 4));
                ch.pipeline().addLast(new CommandCallbackHandler(connection, receiver));
                ch.pipeline().addLast(new CommandOutBoundHandler());
            }
        });

        return connection;


    }


    private void sync() {

        long time = 0;
        while (true) {
            boolean connected = Arrays.stream(connections).anyMatch(ClientConnection::isConnected);
            if (connected || time > 1000 * 10) {
                return;
            }
            try {
                time += 100;
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

    }

    private ClientConnection searchInConnections(InetSocketAddress address) {
        for (ClientConnection connection : connections) {
            if (connection.remoteAddress().equals(address))
                return connection;
        }
        return null;
    }


    private ClientConnection nextConnection() {
        ClientConnection[] connections = this.connections;

        int index = (connectionIndex + 1) % connections.length;

        int firstIndex = index;

        do {
            ClientConnection conn = connections[index];
            if (!conn.isConnected()) {
                index = (++index) % connections.length;
            } else {
                connectionIndex = index;
                return conn;
            }
        } while (index != firstIndex);


        connectionIndex = index;


        //此处应该抛出异常
        throw new RuntimeException("hhhhhssxxsddds");
    }

    private List<ClientConnection> activeConnections() {
        return Arrays.stream(connections).filter(ClientConnection::isConnected).collect(Collectors.toList());

    }

    public <T> T write(CommandCode cmd, Type t, Object... params) {


        try {
            Future<T> future = stream(cmd, null, new TypeConverter<>(t), params);

            return future.get(10, TimeUnit.SECONDS);
        }
        catch (InterruptedException ignored){
            return null;
        }

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }


    public <T> Map<ClientConnection,T> writeAll(CommandCode cmd, Type t, Object... params) {


        Map<ClientConnection, T> map = new HashMap<>();
        try {

            List<Future<T>> futures = new LinkedList<>();
            for (ClientConnection conn : activeConnections()) {
                Future<T> future = stream(cmd, null, new TypeConverter<>(t), params);

                map.put(conn, future.get());
            }

            return map;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    public <T> Future<T> stream(CommandCode cmd, Consumer<T> consumer, Converter<byte[], T> converter, Object... params) {
        return stream(nextConnection(), cmd, consumer, converter, params);

    }


    public <T> Future<T> stream(ClientConnection connection, CommandCode cmd, Consumer<T> consumer, Converter<byte[], T> converter, Object... params) {
        if(connection==null) connection = nextConnection();
        long id = ID.incrementAndGet();

        Future<T> future = receiver.watch(id, consumer, converter);

        connection.write(new Chunk(id, (short) cmd.ordinal(), params));

        return future;
    }
}
