package com.bh.spider.scheduler.context;

import com.bh.spider.transfer.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lq on 17-4-8.
 */
public class ClientContext implements Context {
    private static final Logger logger = LoggerFactory.getLogger(ClientContext.class);

    private ChannelHandlerContext source = null;
    private volatile Boolean enable = true;

    private Object buffer = null;

    private long id;

    public ClientContext(long id, ChannelHandlerContext source) {
        this.id = id;
        this.source = source;
    }


    @Override
    public synchronized void write(Object data) {
        flush();
        buffer = data;
    }

    @Override
    public synchronized void complete() {

        if (!isWriteEnable()) return;
        try {
            write0(id, (byte) 0, buffer != null ? JsonFactory.get().writeValueAsBytes(buffer) : null);
            enable = false;
        } catch (JsonProcessingException e) {
            exception(e);
        }


    }

    private synchronized void write0(long id, byte flag, byte[] data) {
        if (data == null) data = new byte[0];
        ByteBuf buf = source.alloc().buffer(8 + 1 + 4 + (data.length));//id,flag,len,data
        buf.writeLong(id);
        buf.writeByte(flag);
        buf.writeInt(data.length);
        buf.writeBytes(data);
        source.writeAndFlush(buf);
    }

    @Override
    public synchronized boolean isWriteEnable() {
        return enable && this.source.channel().isOpen();
    }

    @Override
    public synchronized void exception(Throwable cause) {
        write0(id, (byte) 0x02, cause.getMessage().getBytes());
        enable = false;
    }

    @Override
    public synchronized void flush() {

        if (isWriteEnable() && buffer != null) {
            try {
                write0(id, (byte) 1, JsonFactory.get().writeValueAsBytes(buffer));
                buffer = null;
            } catch (Exception e) {
                exception(e);
            }
        }
    }

}
