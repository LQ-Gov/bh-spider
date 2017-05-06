package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lq on 17-5-5.
 */
public class StringInterpreter extends UniqueInterpreter<String> {
    @Override
    public boolean support(Class cls) {
        return support(cls, String.class);
    }

    @Override
    protected byte[] fromArray(String[] input) {
        return fromCollection(Arrays.asList(input));
    }

    @Override
    protected byte[] fromCollection(Collection<String> collection) {
        int len = 0;
        for (String str : collection) {
            len += str == null ? 1 : str.length()+5;
        }

        ByteBuffer buffer = ByteBuffer.allocate(len);

        collection.forEach(x ->buffer.put(fromObject(x)));

        return buffer.array();
    }

    @Override
    protected byte[] fromObject(String o) {
        if (o == null) return new byte[]{DataTypes.NULL.value()};
        return ByteBuffer.allocate(1 + 4 + o.length())
                .put(DataTypes.STRING.value())
                .putInt(o.length()).put(o.getBytes()).array();
    }

    @Override
    protected String[] toArray(byte[] data, int pos, int len) throws Exception {
        List<String> list = new LinkedList<>();

        toCollection(list, data, pos, len);

        String[] result = new String[list.size()];
        list.toArray(result);
        return result;
    }

    @Override
    protected void toCollection(Collection<String> collection, byte[] data, int pos, int len) throws Exception {
        int end = pos + len;

        while (pos < end) {
            String str = toObject(data, pos, end - pos);
            collection.add(str);
            if (str == null) pos++;
            else pos = pos + str.length() + 5;
        }
    }

    @Override
    protected String toObject(byte[] data, int pos, int len) throws Exception {
        if (data[pos] == DataTypes.NULL.value()) return null;
        int size = ByteBuffer.wrap(data, pos + 1, 4).getInt();

        if (size > len - 5) throw new Exception("error len");

        return new String(data, pos + 5, size);
    }
}
