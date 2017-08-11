package com.charles.spider.client.converter;


import com.charles.common.JsonFactory;
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
        ObjectMapper mapper = JsonFactory.get();

        return type==null?null: mapper.readValue(data, mapper.constructType(type));
    }
}
