package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;
import com.charles.spider.common.protocol.Protocol;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by lq on 17-5-8.
 */
public class EnumInterpreter extends AbstractInterpreter<Enum> {

    @Override
    public boolean support(Class cls) {
        return support(cls, Enum.class);
    }

    @Override
    protected byte[] fromArray(Enum[] input) throws Exception {
        return fromCollection(Arrays.asList(input));
    }

    @Override
    protected byte[] fromCollection(Collection<Enum> collection) throws Exception {
        int len = 0;
        for (Enum e : collection) {
            len += e == null ? 1 : e.name().length() + 5;
        }

        ByteBuffer buffer = ByteBuffer.allocate(ARRAY_HEAD_LEN + len);
        buffer.put(DataTypes.ENUM.value()).putInt(len);

        collection.forEach(x -> buffer.put(fromObject(x)));

        return buffer.array();
    }

    @Override
    protected byte[] fromObject(Enum o) {
        return ByteBuffer.allocate(1 + 4 + o.name().length()).put(DataTypes.ENUM.value())
                .putInt(o.name().length())
                .put(o.name().getBytes())
                .array();
    }

    @Override
    protected Enum[] toArray(Class<Enum> cls, byte[] data, int pos, int len) throws Exception {
        List<Enum> list = new ArrayList<>();
        toCollection(cls, list, data, pos, len);
        return (Enum[]) list.toArray();
    }

    @Override
    protected void toCollection(Class<Enum> cls, Collection<Enum> collection, byte[] data, int pos, int len) throws Exception {
        int end = pos + len;

        while (pos < end) {
            Enum e = toObject(cls, data, pos, end - pos);
            collection.add(e);
            if (e == null) pos++;
            else pos = pos + e.name().length() + 5;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Enum toObject(Class<Enum> cls, byte[] data, int pos, int len) throws Exception {
        if (data[pos] == DataTypes.NULL.value()) return null;
        int size = ByteBuffer.wrap(data, pos + 1, 4).getInt();

        if (size > len - 5) throw new Exception("error len");

        String name = new String(data, pos + 5, size);

        return Enum.valueOf(cls, name);
    }
}
