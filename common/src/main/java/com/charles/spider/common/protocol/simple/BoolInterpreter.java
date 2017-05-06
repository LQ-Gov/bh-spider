package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.Collection;

/**
 * Created by lq on 17-5-3.
 */
public class BoolInterpreter extends UniqueInterpreter<Boolean> {
    @Override
    public boolean support(Class cls) {
        return support(cls,Boolean.class,boolean.class);
    }

    @Override
    protected byte[] fromArray(Boolean[] input) {
        BitSet bits = new BitSet(input.length);
        for (int i = 0; i < input.length; i++) bits.set(i, input[i]);
        byte[] data = bits.toByteArray();
        return ByteBuffer.allocate(1 + data.length).put((byte) (input.length % 8)).put(data).array();
    }

    @Override
    protected byte[] fromCollection(Collection<Boolean> collection) {
        BitSet bits = new BitSet(collection.size());
        int pos = 0;

        for(Boolean it:collection) bits.set(pos++,it);
        return bits.toByteArray();
    }

    @Override
    protected byte[] fromObject(Boolean o) {
        byte[] result = new byte[2];
        result[0] = DataTypes.BOOL.value();
        result[1] = (byte) (o ? 1 : 0);
        return result;
    }

    @Override
    protected Boolean[] toArray(byte[] data, int pos, int len) {
        byte last = data[pos];
        Boolean[] result = new Boolean[(len-2)*8+last];
        int index =0;
        for(byte b = data[++pos];pos<pos+len;b = data[pos++]) {
            while (index==0||index%8!=0) {
                result[index++] = (b & 0xf0) > 0;
                b = (byte) (b << 1);
            }
        }

        byte b = data[pos+len];
        while (index<result.length) {
            result[index++] = (b & 0xf0) > 0;
            b = (byte) (b << 1);
        }

        return result;


    }

    @Override
    protected void toCollection(Collection<Boolean> collection, byte[] data, int pos, int len) {
        int count = (len - 2) * 8 + data[pos], index = 0;
        for (byte b = data[++pos]; pos < pos + len; b = data[pos++]) {
            while (index == 0 || index % 8 != 0) {
                collection.add((b & 0xf0) > 0);
                b = (byte) (b << 1);
                index++;
            }
        }

        byte b = data[pos + len];
        while (index < count) {
            collection.add((b & 0xf0) > 0);
            b = (byte) (b << 1);
            index++;
        }
    }

    @Override
    protected Boolean toObject(byte[] data, int pos, int len) {
        return data[pos + 1] > 0;
    }
}
