package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by lq on 17-5-4.
 */
public class LongInterpreter extends UniqueInterpreter<Long> {
    @Override
    public boolean support(Type cls) {
        return support((Class<?>)cls,Long.class,long.class);
    }

    @Override
    protected byte[] fromArray(Long[] input) {
//        ByteBuffer buffer = ByteBuffer.allocate(ARRAY_HEAD_LEN+input.length*8);
//        buffer.put(DataTypes.LONG.value()).putInt(input.length*8);
//        Arrays.stream(input).forEach(buffer::putLong);
//        return buffer.array();

        return fromCollection(Arrays.asList(input));
    }

    @Override
    protected byte[] fromCollection(Collection<Long> collection) {
        ByteBuffer buffer = ByteBuffer.allocate(ARRAY_HEAD_LEN + collection.size() * 8);
        buffer.put(DataTypes.ARRAY.value()).putInt(collection.size() * 8 + 1);
        buffer.put(DataTypes.LONG.value());
        collection.forEach(buffer::putLong);
        return buffer.array();
    }

    @Override
    protected byte[] fromObject(Long o) {
        return ByteBuffer.allocate(9).put(DataTypes.LONG.value()).putLong(o).array();
    }

    @Override
    protected Long[] toArray(byte[] data, int pos, int len) {
        ByteBuffer buffer = ByteBuffer.wrap(data, pos, len);
        int sum = len / 8;

        Long[] result = new Long[sum];
        for (int i = 0; i < sum; i++) result[i] = buffer.getLong();
        return result;
    }

    @Override
    protected void toCollection(Collection<Long> collection, byte[] data, int pos, int len) {
        ByteBuffer buffer = ByteBuffer.wrap(data, pos, len);
        int sum = len /8;
        for(int i=0;i<sum;i++) collection.add(buffer.getLong());

    }

    @Override
    protected Long toObject(byte[] data, int pos, int len) {
        return ByteBuffer.wrap(data,pos+1,len-1).getLong();
    }
}
