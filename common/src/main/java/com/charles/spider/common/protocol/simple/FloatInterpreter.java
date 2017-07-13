package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by lq on 17-5-4.
 */
public class FloatInterpreter extends UniqueInterpreter<Float> {
    @Override
    public boolean support(Type cls) {
        return support((Class<?>)cls,Float.class,float.class);
    }

    @Override
    protected byte[] fromArray(Float[] input) {
        return fromCollection(Arrays.asList(input));
    }

    @Override
    protected byte[] fromCollection(Collection<Float> collection) {
        ByteBuffer buffer = ByteBuffer.allocate(ARRAY_HEAD_LEN+ collection.size()*4);
        buffer.put(DataTypes.ARRAY.value());
        buffer.putInt(collection.size()*4+1);
        buffer.put(DataTypes.FLOAT.value());
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
        ByteBuffer buffer = ByteBuffer.wrap(data, pos, len);
        for (int i = 0; i < len / 4; i++) collection.add(buffer.getFloat());

    }

    @Override
    protected Float toObject(byte[] data, int pos, int len) {
        return ByteBuffer.wrap(data,pos+1,len-1).getFloat();
    }
}
