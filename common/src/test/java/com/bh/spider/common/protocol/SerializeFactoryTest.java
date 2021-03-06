package com.bh.spider.common.protocol;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-5-18.
 */
public class SerializeFactoryTest {

    private void serializHelper(Object... input) throws Exception {
        byte[] data = SerializeFactory.serialize(input);
        Object[] result = SerializeFactory.deserialize(data,null);
        Assertions.assertArrayEquals(input,result);
    }

    @Test
    public void serialize() throws Exception {
        serializHelper(1,true,"abc");
        serializHelper(null,null,null);

    }

    @Test
    public void deserialize() throws Exception {
        Map<String,Integer> list = new HashMap<>();
        Type type = list.getClass();
        Map<TypeVariable<?>, Type> variables = TypeUtils.getTypeArguments(
                (ParameterizedType) (type instanceof Class<?> ? ((Class) type).getGenericSuperclass() : type));

        int a =0;
    }

}