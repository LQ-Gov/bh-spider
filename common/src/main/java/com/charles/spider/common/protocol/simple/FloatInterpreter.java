package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by lq on 17-5-4.
 */
public class FloatInterpreter extends UniqueInterpreter<Float> {
    @Override
    public boolean support(Class cls) {
        return support(cls,Float.class,float.class);
    }

    @Override
    protected byte[] fromArray(Float[] input) {
        ByteBuffer buffer = ByteBuffer.allocate(4*input.length);
        Arrays.stream(input).forEach(buffer::putFloat);
        return buffer.array();
    }

    @Override
    protected byte[] fromCollection(Collection<Float> collection) {
        ByteBuffer buffer = ByteBuffer.allocate(collection.size()*4);
        collection.forEach(buffer::putFloat);
        return buffer.array();
    }

    @Override
    protected byte[] fromObject(Float o) {
        return ByteBuffer.allocate(5).put(DataTypes.FLOAT.value()).putFloat(o).array();
    }

    @Override
    protected Float[] toArray(byte[] data, int pos, int len) {
        ByteBuffer buffer = ByteBuffer.wrap(data,pos,len);
        Float[] result = new Float[len/4];
        for(int i=0;i<result.length;i++) result[i]=buffer.getFloat();
        return result;
    }

    @Override
    protected void toCollection(Collection<Float> collection, byte[] data, int pos, int len) {
        ByteBuffer buffer = ByteBuffer.wrap(data,pos,len);
        for(int i=0;i<len/4;i++) collection.add(buffer.getFloat());

    }

    @Override
    protected Float toObject(byte[] data, int pos, int len) {
        return ByteBuffer.wrap(data,pos+1,len-1).getFloat();
    }
}
