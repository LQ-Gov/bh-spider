package com.charles.spider.common.protocol;

import java.nio.charset.Charset;

/**
 * Created by LQ on 2015/10/20.
 */
public class ProtocolBase implements Protocol {
    @Override
    public byte[] pack(int data) {
        return new byte[0];
    }

    @Override
    public byte[] pack(boolean data) {
        return new byte[0];
    }

    @Override
    public byte[] pack(float data) {
        return new byte[0];
    }

    @Override
    public byte[] pack(double data) {
        return new byte[0];
    }

    @Override
    public byte[] pack(long data) {
        return new byte[0];
    }

    @Override
    public byte[] pack(char data) {
        return new byte[0];
    }

    @Override
    public byte[] pack(byte data) {
        return new byte[0];
    }

    @Override
    public byte[] pack(String data, Charset charset) throws Exception {
        return new byte[0];
    }

    @Override
    public <T> byte[] pack(T data) throws Exception {
        if (data instanceof ProtocolObject)
            return ((ProtocolObject) data).toBytes();
        return new byte[0];
    }

    @Override
    public <T> byte[] pack(T[] data) throws Exception {
        return new byte[0];
    }


    @Override
    public Assemble assemble(byte[] data, int pos, int len) throws Exception {
        return null;
    }
}
