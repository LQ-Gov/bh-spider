package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;

/**
 * Created by lq on 17-5-3.
 */
class BoolInterpreter extends UniqueInterpreter<Boolean> {
    @Override
    public boolean support(Class cls) {
        return support(cls,Boolean.class,boolean.class);
    }

    @Override
    protected byte[] fromArray(Boolean[] input) {
        return fromCollection(Arrays.asList(input));
    }


    @Override
    protected byte[] fromCollection(Collection<Boolean> collection) {
        BitSet bits = new BitSet(collection.size());
        int pos = 0;

        for (Boolean it : collection) bits.set(pos++, it);
        byte[] data = bits.toByteArray();
        byte last = (byte) (collection.size() % 8);
        last = last == 0 && collection.size() > 0 ? 8 : last;
        return ByteBuffer.allocate(ARRAY_HEAD_LEN + 1 + data.length)
                .put(DataTypes.BOOL.value())
                .putInt(1 + data.length)
                .put(last)
                .put(data)
                .array();
    }

    @Override
    protected byte[] fromObject(Boolean o) {
        byte[] result = new byte[2];
        result[0] = DataTypes.BOOL.value();
        result[1] = (byte) (o ? 1 : 0);
        return result;
    }

    @Override
    protected Boolean[] toArray(byte[] data, int pos, int len) throws Exception {
        byte last = data[pos];

        if (last == 0) return new Boolean[0];
        Boolean[] result = new Boolean[(len - 2) * 8 + last];
        int index = 0, end = pos + len - 1;
        for (byte b = data[++pos]; pos < end; b = data[++pos]) {
            while (index == 0 || index % 8 != 0) {
                result[index++] = (b & 0x01) > 0;
                b = (byte) (b >> 1);
            }
        }

        byte b = data[end];
        while (index < result.length) {
            result[index++] = (b & 0x01) > 0;
            b = (byte) (b >> 1);
        }

        return result;
    }

    @Override
    protected void toCollection(Collection<Boolean> collection, byte[] data, int pos, int len) {
        byte last = data[pos];
        if (last == 0) return;
        int index = 0, end = pos + len - 1;
        for (byte b = data[++pos]; pos < end; b = data[++pos]) {
            while (index == 0 || index % 8 != 0) {
                collection.add((b & 0x01) > 0);
                b = (byte) (b >> 1);
                index++;
            }
        }

        byte b = data[end];
        index = 0;
        while (index < last) {
            collection.add((b & 0x01) > 0);
            b = (byte) (b >> 1);
            index++;
        }
    }

    @Override
    protected Boolean toObject(byte[] data, int pos, int len) {
        return data[pos + 1] > 0;
    }
}
