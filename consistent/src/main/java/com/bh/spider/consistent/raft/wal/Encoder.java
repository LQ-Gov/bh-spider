package com.bh.spider.consistent.raft.wal;

import com.bh.spider.consistent.raft.wal.pb.Record;
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

    public void encode(Record.Builder recordBuilder) throws IOException {

        long value = CrcUtils.sum32(recordBuilder.getData().toByteArray());

        recordBuilder.setCrc((int) value);


        byte[] data = recordBuilder.build().toByteArray();

        long dataLength = data.length;
        long padLength = computePadLength(dataLength);

        if (padLength != 0) {
            dataLength |= (0x80 | padLength) << 56;
        }


        ByteBuffer buffer = ByteBuffer.allocate((int) (8 + dataLength + padLength))
                .putLong(dataLength).put(data).put(new byte[(int) padLength]);

        synchronized (this) {
            this.channel.write(buffer);
        }

    }

    private long computePadLength(long dataLength) {

        // force 8 byte alignment so length never gets a torn write

        return (8 - (dataLength % 8)) % 8;
    }


    public void flush() throws IOException {
        this.channel.force(true);
    }

}
