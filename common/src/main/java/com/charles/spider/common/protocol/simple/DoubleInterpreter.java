package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by lq on 17-5-4.
 */
    public class DoubleInterpreter extends UniqueInterpreter<Double> {
    @Override
    public boolean support(Class cls) {
        return support(cls,Double.class,double.class);
    }

    @Override
    protected byte[] fromArray(Double[] input) {
        ByteBuffer buffer = ByteBuffer.allocate(8*input.length);
        Arrays.stream(input).forEach(buffer::putDouble);
        return buffer.array();
    }

    @Override
    protected byte[] fromCollection(Collection<Double> collection) {
        ByteBuffer buffer = ByteBuffer.allocate(collection.size()*8);
        collection.forEach(buffer::putDouble);
        return buffer.array();
    }

    @Override
    protected byte[] fromObject(Double o) {
        return ByteBuffer.allocate(9).put(DataTypes.DOUBLE.value()).putDouble(o).array();
    }

    @Override
    protected Double[] toArray(byte[] data, int pos, int len) {
        ByteBuffer buffer = ByteBuffer.wrap(data,pos,len);
        Double[] result = new Double[len/8];
        for(int i=0;i<result.length;i++) result[i]=buffer.getDouble();
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
