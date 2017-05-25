package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.*;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lq on 17-5-6.
 */
class ArrayInterpreter extends AbstractInterpreter<Object> {
    private Protocol protocol = null;
    private InterpreterFactory interpreterFactory = null;

    public ArrayInterpreter(Protocol protocol, InterpreterFactory factory) {

        this.protocol = protocol;
        this.interpreterFactory = factory;
    }

    @Override
    public boolean support(Class cls) {
        return cls != null && (cls.isArray() || cls == Object.class);
    }

    @Override
    protected byte[] fromArray(Object[] input) throws Exception {
        return fromCollection(Arrays.asList(input));

    }

    @Override
    protected byte[] fromCollection(Collection<Object> collection) throws Exception {

        throw new Exception("not support");
    }

    @Override
    protected byte[] fromObject(Object o) throws Exception {
        if (!o.getClass().isArray()) throw new Exception("error type");
        List<byte[]> list = new LinkedList<>();
        int len = Array.getLength(o), bytes_total_count = 0;

        for (int i = 0; i < len; i++) {
            Object item = Array.get(o, i);
            byte[] bytes;
            if (item.getClass().isArray()) bytes = fromObject(item);
            else bytes = protocol.pack(item);

            bytes_total_count += bytes.length;
            list.add(bytes);
        }
        ByteBuffer buffer = ByteBuffer.allocate(1 + 4 + 1 + bytes_total_count)
                .put(DataTypes.ARRAY.value())
                .putInt(bytes_total_count)
                .putInt(DataTypes.type(o.getClass().getComponentType()).value());

        list.forEach(buffer::put);

        return buffer.array();
    }

    @Override
    protected Object[] toArray(Class<Object> cls, byte[] data, int pos, int len) throws Exception {
        //return new Object[0];
        throw new Exception("not support");
    }

    @Override
    protected void toCollection(Class<Object> cls, Collection<Object> collection, byte[] data, int pos, int len) throws Exception {
        throw new Exception("not support");
    }

    @Override
    protected Object toObject(Class<Object> cls, byte[] data, int pos, int len) throws Exception {

        if(cls==null||cls == Object.class) cls = Object.class;

        int size = ByteBuffer.wrap(data, pos + 1, 4).getInt();
        if (size + 5 > len) throw new Exception("error len");

        DataTypes t = DataTypes.type(data[pos + 5]);

        if (cls != Object.class && DataTypes.type(cls.getComponentType()) != t)
            throw new UnSupportTypeException(cls);

        if (t != DataTypes.ARRAY){
            //执行 解析代码
        }
        else
            return interpreterFactory.get(t).unpack(cls, data, pos + 6, size - 1);


        Assemble assemble = protocol.assemble(data, pos + 5, size);


        List list = new LinkedList();

        Token token;

        while ((token = assemble.next()) != null) {
            list.add(token.toClass(Object.class));
        }

        Object result = Array.newInstance(cls, list.size());

        for (int i = 0; i < list.size(); i++) {
            Array.set(result, i, list.get(i));
        }

        return result;
    }


}
