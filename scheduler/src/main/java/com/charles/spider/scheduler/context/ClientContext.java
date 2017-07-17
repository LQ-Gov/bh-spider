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


    public ClientContext(ChannelHandlerContext source) {

        this.source = source;
    }


    @Override
    public synchronized void write(Object data) {
        if (!IsWriteEnable())
            return;

        buffer = data;

    }

    @Override
    public synchronized void complete() {

        try {
            byte[] data = JsonFactory.get().writeValueAsBytes(buffer);
            ByteBuf buf = source.alloc().buffer(5 + data.length);
            buf.writeBoolean(true);
            buf.writeInt(data.length);
            buf.writeBytes(data);

            source.writeAndFlush(buf);

            enable = false;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stream() {

    }

    @Override
    public boolean IsWriteEnable() {
        return enable;
    }
}
