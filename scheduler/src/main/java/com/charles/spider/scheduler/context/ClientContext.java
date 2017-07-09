package com.charles.spider.scheduler.context;

import com.charles.spider.common.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by lq on 17-4-8.
 */
public class ClientContext implements Context {
    private ChannelHandlerContext source =null;
    private volatile Boolean enable = true;

    private Protocol protocol=null;

    public ClientContext(ChannelHandlerContext source, Protocol protocol){

        this.source=source;
        this.protocol = protocol;
    }


    @Override
    public synchronized void write(Object data) {
        if (IsWriteEnable())
            return;

        try {
            byte[] bytes = protocol.pack(data);
            ByteBuf buf = source.alloc().buffer(1+4+bytes.length);

            buf.writeBoolean(true);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
            source.writeAndFlush(buf);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void finish() {
        enable=false;

        //write(null,true);
    }

    @Override
    public void stream() {

    }

    @Override
    public boolean IsWriteEnable() {
        return enable;
    }
}
