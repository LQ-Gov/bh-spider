package com.bh.spider.common.protocol.simple;

import com.bh.spider.common.protocol.DataTypes;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by lq on 17-4-27.
 */
public class ByteInterpreter extends UniqueInterpreter<Byte> {
    @Override
    public boolean support(Type cls) {
        return support((Class<?>) cls, Byte.class, byte.class);
    }

    @Override
    protected byte[] fromArray(Byte[] input) {
        ByteBuffer buffer = ByteBuffer.allocate(ARRAY_HEAD_LEN + input.length)
                .put(DataTypes.ARRAY.value())
                .putInt(input.length + 1)
                .put(DataTypes.BYTE.value());
        Arrays.stream(input).forEach(buffer::put);

        return buffer.array();
    }

    @Override
    protected byte[] fromCollection(Collection<Byte> collection) {
        byte[] result = new byte[ARRAY_HEAD_LEN + collection.size()];

        result[0] = DataTypes.ARRAY.value();
        System.arraycopy(ByteBuffer.allocate(4).putInt(collection.size()).array(), 0, result, 1, 4);
        result[5] = DataTypes.BYTE.value();

        int index = ARRAY_HEAD_LEN;
        for (Byte it : collection) result[index++] = it;

        return result;
    }

    @Override
    protected byte[] fromObject(Byte o) {
        byte[] result = new byte[2];
        result[0] = DataTypes.BYTE.value();
        result[1] = o;
        return result;

    }

    @Override
    protected Byte[] toArray(byte[] data, int pos, int len) {
        Byte[] result = new Byte[len];
        for (int i = pos; i < pos + len; i++)
            result[i - pos] = data[i];
        return result;
    }

    @Override
    protected void toCollection(Collection<Byte> collection, byte[] data, int pos, int len) {
        for (int i = pos; i < pos + len; i++)
            collection.add(data[i]);

    }

    @Override
    protected Byte toObject(byte[] data, int pos, int len) {
        return data[pos + 1];
    }
}
