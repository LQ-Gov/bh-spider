package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by lq on 17-5-3.
 */
public class CharInterpreter extends UniqueInterpreter<Character> {
    @Override
    public boolean support(Class cls) {
        return support(cls, Character.class, char.class);
    }

    @Override
    protected byte[] fromArray(Character... input) {
        return fromCollection(Arrays.asList(input));
    }

    @Override
    protected byte[] fromCollection(Collection<Character> collection) {
        ByteBuffer buffer = ByteBuffer.allocate(ARRAY_HEAD_LEN + collection.size() * 2);
        buffer.put(DataTypes.CHAR.value()).putInt(collection.size()*2);
        collection.forEach(buffer::putChar);
        return buffer.array();
    }

    @Override
    protected byte[] fromObject(Character o) {
        return ByteBuffer.allocate(3).put(DataTypes.CHAR.value()).putChar(o).array();
    }

    @Override
    protected Character[] toArray(byte[] data, int pos, int len) {
        ByteBuffer buffer = ByteBuffer.wrap(data, pos, len);


        int sum = (len) / 2;
        Character[] result = new Character[sum];
        for (int i = 0; i < sum; i++) result[i] = buffer.getChar();
        return result;
    }

    @Override
    protected void toCollection(Collection<Character> collection, byte[] data, int pos, int len) {
        ByteBuffer buffer = ByteBuffer.wrap(data,pos,len);

        int sum = (len)/2;

        for(int i=0;i<sum;i++) collection.add(buffer.getChar());

    }

    @Override
    protected Character toObject(byte[] data, int pos, int len) {
        return ByteBuffer.wrap(data,pos+1,len-1).getChar();
    }
}
