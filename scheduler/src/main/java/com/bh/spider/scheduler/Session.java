package com.bh.spider.scheduler;

import com.bh.spider.scheduler.event.Command;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.Json;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

public class Session implements Closeable {
    private long id;
    private Channel channel;

    private List<Consumer<Session>> closeConsumers = new Vector<>();

    public Session(Channel channel){
        this.channel = channel;
        this.id = IdGenerator.instance.nextId();

    }


    public long id(){
        return id;
    }

    public void write(Command cmd) throws JsonProcessingException {
        short cmdCode = (short) CommandCode.valueOf(cmd.key()).ordinal();

        byte[] data = Json.get().writeValueAsBytes(cmd.params());

        ByteBuf buffer = channel.alloc().buffer(8+2+4+data.length);
        buffer.writeLong(id).writeShort(cmdCode).writeInt(data.length).writeBytes(data);

        channel.writeAndFlush(buffer);

    }



    public void whenClose(Consumer<Session> consumer){
        if(consumer!=null) {
            closeConsumers.add(consumer);
        }
    }

    @Override
    public void close() throws IOException {
        for (Consumer<Session> consumer : closeConsumers) {
            consumer.accept(this);
        }
    }
}
