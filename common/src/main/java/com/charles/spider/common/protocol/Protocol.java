package com.charles.spider.common.protocol;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

/**
 * Created by LQ on 2015/10/20.
 */
public interface Protocol {

    static Type getGenericType(ParameterizedType parameterizedType) {
        return parameterizedType.getActualTypeArguments()[0];
    }

    static Type getGenericType(Type type){
        if(type instanceof Class<?>){
            ParameterizedType parameterizedType = (ParameterizedType) ((Class) type).getGenericSuperclass();
            return getGenericType(parameterizedType);
        }
        else if(type instanceof ParameterizedType)
            return getGenericType((ParameterizedType) type);

        return type;
    }

    byte[] pack(Object data) throws Exception;

    Assemble assemble(byte[] data, int pos, int len) throws Exception;
}
