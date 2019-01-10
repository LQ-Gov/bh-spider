package com.bh.spider.client.converter;


import com.bh.spider.transfer.Json;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Type;

public class TypeConverter<T> implements Converter<byte[], T> {

    private Type type;

    public TypeConverter(Type type) {
        this.type = type;
    }

    @Override
    public T convert(byte[] data) throws IOException {
        ObjectMapper mapper = Json.get();

        return type==null?null: mapper.readValue(data, mapper.constructType(type));
    }
}
