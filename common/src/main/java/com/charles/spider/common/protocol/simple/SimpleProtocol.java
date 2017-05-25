package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.*;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by LQ on 2015/10/20.
 */
public final class SimpleProtocol implements Protocol {
    private final static int MAX_LEN = (Integer.MAX_VALUE - 5);

    private volatile static SimpleProtocol ins = new SimpleProtocol();

    private InterpreterFactory interpreterFactory = new InterpreterFactory(this);


    public static SimpleProtocol instance(){
        return ins;
    }

    /**
     * pack data
     * @param o
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> byte[] pack(T o) throws Exception {
        if (o == null) return interpreterFactory.get(DataTypes.NULL).pack(o);
        Class<?> cls = o.getClass();
        if (cls.isArray()) cls = cls.getComponentType();

        else if (Collection.class.isAssignableFrom(cls)) {
            ParameterizedType parameterizedType = (ParameterizedType) o.getClass().getGenericSuperclass();
            cls = (Class) parameterizedType.getActualTypeArguments()[0];
        }


        DataTypes t = DataTypes.type(cls);

        return interpreterFactory.get(t).pack(o);
    }






    @Override
    public Assemble assemble(byte[] data, int pos, int len) throws Exception {
        return new SimpleAssemble(data, pos, len);
    }
}
