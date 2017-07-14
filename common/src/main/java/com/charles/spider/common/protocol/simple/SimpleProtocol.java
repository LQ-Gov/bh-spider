package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.*;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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


    public static SimpleProtocol instance() {
        return ins;
    }

    /**
     * pack data
     *
     * @param o
     * @return
     * @throws Exception
     */
    public byte[] pack(Object o) throws Exception {
        //null
        //基本类型

        //Array
        //Object
        if (o == null) return interpreterFactory.get(DataTypes.NULL).pack(o);


        DataTypes type;

        Class<?> cls = o.getClass();

        if(cls.isArray()) type = DataTypes.ARRAY;

        else if (Collection.class.isAssignableFrom(cls)) type=DataTypes.ARRAY;


        //if (cls.isArray()) type = DataTypes.type(cls.getComponentType());

//        else if (Collection.class.isAssignableFrom(cls)) {
//            ParameterizedType parameterizedType = (ParameterizedType) o.getClass().getGenericSuperclass();
//            Type t = parameterizedType.getActualTypeArguments()[0];
//            if (t instanceof Class<?>)
//                type = DataTypes.type((Class<?>) t);
//            else
//                type = DataTypes.CLASS;
//        }



        else type = DataTypes.type(o.getClass());


        return interpreterFactory.get(type).pack(o);
    }


    @Override
    public Assemble assemble(byte[] data, int pos, int len) throws Exception {
        return new SimpleAssemble(data, pos, len);
    }
}
