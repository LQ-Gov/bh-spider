package com.charles.spider.client.converter;


import com.charles.spider.client.converter.Converter;

public class DefaultConverter implements Converter<byte[],byte[]> {
    @Override
    public byte[] convert(byte[] data) {
        return data;
    }
}
