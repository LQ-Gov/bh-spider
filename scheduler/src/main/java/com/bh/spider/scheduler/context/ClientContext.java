package com.bh.spider.scheduler.context;

import com.bh.common.utils.Json;
import com.bh.spider.common.fetch.FetchContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Created by lq on 17-4-8.
 */
public class ClientContext extends AbstractCloseableContext implements WatchContext {
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
       byte[] msg = Optional.ofNullable(e).map(Throwable::getMessage).map(String::getBytes).orElse(new byte[0]);

        write0(EXCEPTION_BYTE,msg);
    }

    private synchronized void write0(boolean complete,Object data) {
        try {
            byte[] bytes = data == null ? new byte[0] : Json.get().writeValueAsBytes(data);
            write0(complete ? COMPLETE_BYTE : NOT_COMPLETE_BYTE, bytes);

        } catch (Exception e) {
            write0(e);
        }
    }


    private synchronized void write0(byte flag,byte[] data){
        try{
            ByteBuf buf = source.alloc().buffer(8 + 1 + 4 + data.length);//id,flag,len,data
            buf.writeLong(id);
            buf.writeByte(flag);
            buf.writeInt(data.length);
            buf.writeBytes(data);
            source.channel().writeAndFlush(buf);

        }catch (Exception e){
            e.printStackTrace();
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


    public Channel channel(){
        return source.channel();
    }
}
