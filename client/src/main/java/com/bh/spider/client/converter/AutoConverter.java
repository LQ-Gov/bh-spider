package com.bh.spider.client.converter;

import com.bh.spider.transfer.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

public class AutoConverter<T> implements Converter<byte[],T> {
    @Override
    public T convert(byte[] data) throws IOException {

        Map<TypeVariable<?>, Type> variables = TypeUtils.getTypeArguments((ParameterizedType) this.getClass().getGenericSuperclass());

        Type type = variables.get(this.getClass().getTypeParameters()[0]);

        ObjectMapper mapper = Json.get();

        return mapper.readValue(data,mapper.constructType(type));
    }
}
