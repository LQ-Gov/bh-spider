package com.charles.spider.client.converter;

import java.io.IOException;

public class StringConverter implements Converter<byte[],String> {
    @Override
    public String convert(byte[] data) throws IOException {
        return new String(data);
    }
}
