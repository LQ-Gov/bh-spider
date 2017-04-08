package com.charles.spider.scheduler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by lq on 17-4-8.
 */
public class ClientContext implements Context {
    private ChannelHandlerContext source =null;
    private volatile Boolean enable = true;

    public ClientContext(ChannelHandlerContext source){
        this.source=source;
    }


    @Override
    public synchronized void write(String data) {
        if (IsWriteEnable())
            return;
        write(data, false);
    }

    private void write(String data,boolean flag) {
        ByteBuf completed = source.alloc().buffer(1).writeBoolean(flag);
        source.write(completed);

        if (data == null) return;
        ByteBuf len = source.alloc().buffer(4).writeInt(data.length());
        source.write(len);
        if (data.isEmpty()) return;

        ByteBuf content = source.alloc().buffer(data.length()).writeBytes(data.getBytes());
        source.writeAndFlush(content);
    }

    @Override
    public synchronized void finish() {
        enable=false;

        write(null,true);
    }

    @Override
    public boolean IsWriteEnable() {
        return enable;
    }
}
