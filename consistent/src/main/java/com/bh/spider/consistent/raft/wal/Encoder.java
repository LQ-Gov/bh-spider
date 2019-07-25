package com.bh.spider.consistent.raft.wal;

import com.bh.spider.consistent.raft.serialize.ProtoBufUtils;
import com.bh.spider.consistent.utils.CrcUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author liuqi19
 * @version $Id: Encoder, 2019-04-01 18:10 liuqi19
 */
public class Encoder {
    private FileChannel channel;

    public Encoder(FileChannel channel) {
        this.channel = channel;
    }


    public void encode(Record record) throws IOException {
        long crc32 = CrcUtils.sum32(record.data());

        byte[] data = ProtoBufUtils.serialize(record);

        ByteBuffer buffer = ByteBuffer.allocate(8 + data.length);
        buffer.putLong(data.length).put(data);

        buffer.flip();


        channel.write(buffer);
    }

    public void flush() throws IOException {
        this.channel.force(true);
    }

}
