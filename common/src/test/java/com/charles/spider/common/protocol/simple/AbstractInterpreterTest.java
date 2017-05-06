package com.charles.spider.common.protocol.simple;

import org.junit.Assert;
import org.junit.Before;
import sun.reflect.misc.ReflectUtil;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lq on 17-5-6.
 */
public class AbstractInterpreterTest<T extends UniqueInterpreter> {
    T obj = null;

    @Before
    public void before() throws IllegalAccessException, InstantiationException {
        ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();

        Class<T> cls = (Class<T>) type.getActualTypeArguments()[0];

        obj = cls.newInstance();
    }

    public T interpreter(){
        return obj;
    }

    public <C> void fromArray(C[] input) throws Exception {
        byte[] bytes = interpreter().fromArray(input);

        Assert.assertArrayEquals(interpreter().toArray(bytes,0,bytes.length),input);
    }


    public <C> void  fromCollection(Collection<C> base) throws Exception {
        byte[] bytes = interpreter().fromCollection(base);

        List<Integer> data = new ArrayList<>();
        interpreter().toCollection(data,bytes,0,bytes.length);

        Assert.assertEquals(base,data);
    }


    public <C> void  fromObject(C base) throws Exception {
        byte[] bytes = interpreter().fromObject(base);

        C data  = (C) interpreter().toObject(bytes,0,bytes.length);

        Assert.assertEquals(base,data);
    }
}
