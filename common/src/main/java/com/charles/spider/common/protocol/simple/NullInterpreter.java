package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by lq on 17-5-25.
 */
public class NullInterpreter extends UniqueInterpreter<Object> {
    @Override
    public boolean support(Class cls) {
        return true;
    }

    @Override
    protected Object toObject(byte[] data, int pos, int len) throws Exception {
        return null;
    }

    @Override
    protected Object[] toArray(byte[] data, int pos, int len) throws Exception {
        throw new Exception("not support method");
    }

    @Override
    protected void toCollection(Collection<Object> collection, byte[] data, int pos, int len) throws Exception {
        throw new Exception("not support method");
    }

    @Override
    protected byte[] fromArray(Object[] input) throws Exception {
        return fromCollection(Arrays.asList(input));
    }

    @Override
    protected byte[] fromCollection(Collection<Object> collection) throws Exception {
        throw new Exception("not support method");
    }

    @Override
    protected byte[] fromObject(Object o) throws Exception {
        return new byte[]{DataTypes.NULL.value()};
    }
}
