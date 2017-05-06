package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;

/**
 * Created by lq on 17-4-27.
 */
public class ByteInterpreter extends UniqueInterpreter<Byte> {
    @Override
    public boolean support(Class cls)
    {
        return support(cls,Byte.class,byte.class);
    }

    @Override
    protected byte[] fromArray(Byte[] input) {
        return ArrayUtils.toPrimitive(input);
    }

    @Override
    protected byte[] fromCollection(Collection<Byte> collection) {
        byte[] result = new byte[collection.size()];

        int index = 0;

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
        return ArrayUtils.toObject(data);
    }

    @Override
    protected void toCollection(Collection<Byte> collection, byte[] data, int pos, int len) {
        for(int i = pos;i<pos+len;i++)
            collection.add(data[i]);

    }

    @Override
    protected Byte toObject(byte[] data, int pos, int len) {
        return data[pos+1];
    }
}
