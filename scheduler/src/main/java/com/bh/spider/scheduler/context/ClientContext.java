package com.bh.spider.scheduler.context;

import com.bh.spider.fetch.FetchContext;
import com.bh.spider.transfer.Json;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lq on 17-4-8.
 */
public class ClientContext extends AbstractContext {
    private static final Logger logger = LoggerFactory.getLogger(ClientContext.class);

    private final static byte EXCEPTION_BYTE=0x02;
    private final static byte NOT_COMPLETE_BYTE=0x01;
    private final static byte NOT_COMPLETE_EXCEPTION_BYTE=0x03;
    private final static byte COMPLETE_BYTE=0x00;


    public final static String EVENT_INACTIVE="EVENT_INACTIVE";

    private ChannelHandlerContext source = null;

    private long id;

    public ClientContext(long id, ChannelHandlerContext source) {
        this.id = id;
        this.source = source;
    }

    @Override
    public  void write(Object data) {
        write0(false,data);

    }



    private synchronized void write0(Throwable e) {
        ByteBuf buf = source.alloc().buffer(8+1);//id+state
        buf.writeLong(id);
        buf.writeByte(EXCEPTION_BYTE);
        source.channel().write(buf);
    }

    private synchronized void write0(boolean complete,Object data) {
        try {
            byte[] bytes = data == null ? new byte[0] : Json.get().writeValueAsBytes(data);
            ByteBuf buf = source.alloc().buffer(8 + 1 + 4 + bytes.length);//id,flag,len,data
            buf.writeLong(id);
            buf.writeByte(complete ? COMPLETE_BYTE : NOT_COMPLETE_BYTE);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
            source.channel().write(buf);
            source.channel().flush();
        }catch (Exception e){
            write0(e);
        }
    }


    @Override
    public synchronized void exception(Throwable cause) {
        write0(cause);
    }

    @Override
    public void crawled(FetchContext fetchContext) {

        try {
            write0(true, fetchContext.response());
        } catch (Exception e) {
            write0(e);
        }


    }

    @Override
    public void commandCompleted(Object data) {
        write0(true,data);
    }


    public ChannelId channelId(){
        return source.channel().id();
    }


    public void close(){
        this.complete();
    }

}
