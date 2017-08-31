package com.bh.spider.client.converter;


public class DefaultConverter implements Converter<byte[],byte[]> {
    @Override
    public byte[] convert(byte[] data) {
        return data;
    }
}
