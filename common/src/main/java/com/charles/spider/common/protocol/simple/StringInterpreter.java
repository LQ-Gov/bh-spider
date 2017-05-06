package com.charles.spider.common.protocol.simple;

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
    public boolean support(Object o) {
        return support(o, String.class);
    }

    @Override
    protected byte[] fromArray(String[] input) {
        int len = 0;
        for (String str : input) len += str.length();

        ByteBuffer buffer = ByteBuffer.allocate(len + input.length * 4);

        Arrays.stream(input).forEach(x -> buffer.put(fromObject(x)));

        return buffer.array();
    }

    @Override
    protected byte[] fromCollection(Collection<String> collection) {
        int len = 0;
        for (String str : collection) len += str.length();

        ByteBuffer buffer = ByteBuffer.allocate(len + collection.size() * 4);

        collection.forEach(x -> buffer.put(fromObject(x)));

        return buffer.array();
    }

    @Override
    protected byte[] fromObject(String o) {
        return ByteBuffer.allocate(4+o.length()).putInt(o.length()).put(o.getBytes()).array();
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
            String str = toObject(data, pos, len);
            collection.add(str);
            pos = pos + str.length() + 4;
            len = len - str.length() - 4;
        }

    }

    @Override
    protected String toObject(byte[] data, int pos, int len) throws Exception {
        int size = ByteBuffer.wrap(data, pos, 4).getInt();

        if (size > len - 4) throw new Exception("error len");

        return new String(data, pos + 4, size);
    }
}
