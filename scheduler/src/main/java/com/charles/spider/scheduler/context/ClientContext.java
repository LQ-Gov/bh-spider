package com.charles.spider.scheduler.context;

import com.charles.common.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by lq on 17-4-8.
 */
public class ClientContext implements Context {

    private ChannelHandlerContext source = null;
    private volatile Boolean enable = true;

    private Object buffer = null;

    private long id;

    private boolean stream = false;


    public ClientContext(long id, byte flag, ChannelHandlerContext source) {

        this.id = id;

        stream = flag > 0;

        this.source = source;
    }


    @Override
    public synchronized void write(Object data) {
        if(!isWriteEnable()) return;

        if (!stream) buffer = data;

        else {
            try {

                write0(id, (byte) 1, JsonFactory.get().writeValueAsBytes(data));

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void complete() {

        if (!source.channel().isOpen()) return;

        try {

            write0(id,(byte)0,JsonFactory.get().writeValueAsBytes(buffer));

            enable = false;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isStream() {
        return stream;
    }



    private void write0(long id, byte flag, byte[] data) {
        ByteBuf buf = source.alloc().buffer(8 + 1 + 4 + data.length);//id,flag,len,data
        buf.writeLong(id);
        buf.writeByte(flag);
        buf.writeInt(data.length);
        buf.writeBytes(data);
        source.writeAndFlush(buf);
    }

    @Override
    public boolean isWriteEnable() {
        return enable;
    }
}
