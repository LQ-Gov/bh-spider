package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by lq on 17-4-26.
 */
public class IntInterpreter extends UniqueInterpreter<Integer> {
    @Override
    public boolean support(Class cls) {
        return support(cls,Integer.class,int.class);

    }

    @Override
    protected byte[] fromArray(Integer[] input) {
        ByteBuffer buffer = ByteBuffer.allocate(input.length * 4);
        Arrays.stream(input).forEach(buffer::putInt);
        return buffer.array();
    }

    @Override
    protected byte[] fromCollection(Collection<Integer> collection) {
        ByteBuffer buffer = ByteBuffer.allocate(collection.size()*4);
        collection.forEach(buffer::putInt);
        return buffer.array();
    }

    @Override
    protected byte[] fromObject(Integer o) {
        return ByteBuffer.allocate(5).put(DataTypes.INT.value()).putInt(o).array();
    }

    @Override
    protected Integer[] toArray(byte[] data, int pos, int len) {
        Integer[] result = new Integer[len / 4];
        ByteBuffer buffer = ByteBuffer.wrap(data, pos, len);

        for (int i = 0; i < result.length; i++)
            result[i] = buffer.getInt();

        return result;
    }

    @Override
    protected void toCollection(Collection<Integer> collection, byte[] data, int pos, int len) {
        int sum = len / 4;
        ByteBuffer buffer = ByteBuffer.wrap(data, pos, len);

        for (int i = 0; i < sum; i++)
            collection.add(buffer.getInt());
    }

    @Override
    protected Integer toObject(byte[] data, int pos, int len) {
        return ByteBuffer.wrap(data,pos,len).getInt();
    }
}
