package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.ProtocolObject;

import java.nio.ByteBuffer;

/**
 * Created by lq on 17-4-22.
 */
public class ByteArrayObject implements ProtocolObject<byte[]> {
    private byte[] data = null;

    @Override
    public ProtocolObject<byte[]> write(byte[] input) {
        this.data =input;
        return this;
    }

    @Override
    public byte[] toBytes() {
        return data;
    }

    @Override
    public ProtocolObject<byte[]> write(byte[] input, int pos, int len) {
        return write(null, input, pos, len);
    }

    @Override
    public ProtocolObject<byte[]> write(Class<?> cls, byte[] input, int pos, int len) {
        data = ByteBuffer.wrap(input,pos,len).array();
        return this;
    }

    @Override
    public byte[] toObject() {
        return data;
    }
}
