package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by lq on 17-5-4.
 */
    public class DoubleInterpreter extends UniqueInterpreter<Double> {
    @Override
    public boolean support(Type cls) {
        return support((Class<?>)cls,Double.class,double.class);
    }

    @Override
    protected byte[] fromArray(Double[] input) {
        return fromCollection(Arrays.asList(input));
    }

    @Override
    protected byte[] fromCollection(Collection<Double> collection) {
        ByteBuffer buffer = ByteBuffer.allocate(ARRAY_HEAD_LEN+ collection.size()*8);
        buffer.put(DataTypes.ARRAY.value()).putInt(collection.size()*8+1).put(DataTypes.DOUBLE.value());
        collection.forEach(buffer::putDouble);
        return buffer.array();
    }

    @Override
    protected byte[] fromObject(Double o) {
        return ByteBuffer.allocate(9).put(DataTypes.DOUBLE.value()).putDouble(o).array();
    }

    @Override
    protected Double[] toArray(byte[] data, int pos, int len) {
        ByteBuffer buffer = ByteBuffer.wrap(data, pos, len);
        Double[] result = new Double[len / 8];
        for (int i = 0; i < result.length; i++) result[i] = buffer.getDouble();
        return result;
    }

    @Override
    protected void toCollection(Collection<Double> collection, byte[] data, int pos, int len) {
        ByteBuffer buffer = ByteBuffer.wrap(data,pos,len);
        for(int i=0;i<len/8;i++) collection.add(buffer.getDouble());
    }

    @Override
    protected Double toObject(byte[] data, int pos, int len) {
        return ByteBuffer.wrap(data, pos + 1, len - 1).getDouble();
    }
}
