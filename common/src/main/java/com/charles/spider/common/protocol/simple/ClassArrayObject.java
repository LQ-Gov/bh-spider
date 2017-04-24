package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.Assemble;
import com.charles.spider.common.protocol.ProtocolObject;
import com.charles.spider.common.protocol.Token;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lq on 17-4-23.
 */
public class ClassArrayObject implements ProtocolObject<Object[]> {
    private Object[] data = null;

    @Override
    public ProtocolObject<Object[]> write(Object[] input) {
        this.data = input;
        return this;
    }

    @Override
    public byte[] toBytes() {
        SimpleProtocol instance = SimpleProtocol.instance();
        List<byte[]> result = new ArrayList<>(data.length);
        int len = 0;
        try {
            for (Object it : data) {
                byte[] bytes = instance.pack(it);
                len += bytes.length;
                result.add(bytes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ByteBuffer buffer = ByteBuffer.allocate(len);
        result.forEach(buffer::put);
        return buffer.array();
    }

    @Override
    public ProtocolObject<Object[]> write(byte[] input, int pos, int len) {
        return write(null, input, pos, len);
    }

    @Override
    public ProtocolObject<Object[]> write(Class<?> cls, byte[] input, int pos, int len) {
        Assemble assemble = new SimpleAssemble(input,pos,len);
        List<Object> result = new ArrayList<>();
        Token token;
        try {
            while ((token=assemble.next())!=null){
                result.add(token.toClass(cls));
            }
            data = result.toArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public Object[] toObject() {
        return data;
    }
}
