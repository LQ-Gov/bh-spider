package com.charles.spider.scheduler.context;

import com.charles.spider.common.protocol.Protocol;
import com.charles.spider.common.protocol.SerializeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lq on 17-4-8.
 */
public class ClientContext implements Context {
    private ChannelHandlerContext source =null;
    private volatile Boolean enable = true;

    private List<byte[]> buffer = new LinkedList<>();
    private int bufferCount = 0;


    public ClientContext(ChannelHandlerContext source){

        this.source=source;
    }


    @Override
    public synchronized void write(Object data) {
        if (!IsWriteEnable())
            return;

        try {
            byte[] bytes = SerializeFactory.serialize(data);
            buffer.add(bytes);
            bufferCount += bytes.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void complete() {

        ByteBuf buf = source.alloc().buffer(5+bufferCount);
        buf.writeBoolean(true);
        buf.writeInt(bufferCount);

        buffer.forEach(buf::writeBytes);

        enable = false;

        source.writeAndFlush(buf);

        System.out.println("当然是选择原谅他了");
    }

    @Override
    public void stream() {

    }

    @Override
    public boolean IsWriteEnable() {
        return enable;
    }
}
