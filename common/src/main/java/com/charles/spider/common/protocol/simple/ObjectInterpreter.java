package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.Assemble;
import com.charles.spider.common.protocol.DataTypes;
import com.charles.spider.common.protocol.Protocol;
import com.charles.spider.common.protocol.Token;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lq on 17-5-5.
 */
public class ObjectInterpreter extends AbstractInterpreter<Object> {
    private Protocol protocol = null;
    private InterpreterFactory factory;

    public ObjectInterpreter(Protocol protocol, InterpreterFactory factory) {

        this.protocol = protocol;
        this.factory = factory;

    }


    @Override
    public boolean support(Type cls) {
        return support((Class<?>) cls, Object.class);
    }

    private byte[] fromCollection(DataTypes header, Collection collection) throws Exception {
        List<byte[]> store = new LinkedList<>();

        int len = 0;

        for (Object o : collection) {
            byte[] bytes = fromObject(o);
            len += bytes.length;
            store.add(bytes);
        }

        ByteBuffer buffer = ByteBuffer.allocate(ARRAY_HEAD_LEN + len);
        buffer.put(header.value())
                .putInt(len + 1)
                .put(DataTypes.OBJECT.value());

        store.forEach(buffer::put);

        return buffer.array();
    }

    @Override
    protected byte[] fromArray(Object[] input) throws Exception {
        return fromCollection(DataTypes.ARRAY, Arrays.asList(input));
    }

    @Override
    protected byte[] fromCollection(Collection<Object> collection) throws Exception {
        return fromCollection(DataTypes.COLLECTION, collection);
    }

    @Override
    protected byte[] fromObject(Object o) throws Exception {
        if (o == null) return protocol.pack(null);
        Class<?> cls = o.getClass();

        DataTypes type = DataTypes.type(cls);
        if (type == DataTypes.OBJECT) {
            Field[] fields = cls.getDeclaredFields();
            List<byte[]> list = new LinkedList<>();
            int len = 0;
            for (Field field : fields) {
                byte[] key = protocol.pack(field.getName());
                try {
                    Method get = cls.getDeclaredMethod(get_method_name(field.getName()));

                    if (get != null) {
                        byte[] value = protocol.pack(get.invoke(o));
                        len += key.length + value.length;
                        list.add(key);
                        list.add(value);
                    }
                } catch (NoSuchMethodException ignored) {
                }
            }
            return toBytes(DataTypes.OBJECT, list, len);

        } else return protocol.pack(o);
    }

    @Override
    protected Object[] toArray(Type cls, byte[] data, int pos, int len) throws Exception {
        List<Object> list = new LinkedList<>();
        toCollection(cls, list, data, pos, len);
        return list.toArray(new Object[list.size()]);
    }

    @Override
    protected void toCollection(Type cls, Collection<Object> collection, byte[] data, int pos, int len) throws Exception {
        Assemble assemble = protocol.assemble(data, pos, len);

        Token token;
        while ((token = assemble.next()) != null) {
            collection.add(token.toObject(cls));
        }
    }

    @Override
    protected Object toObject(Type type, byte[] data, int pos, int len) throws Exception {
        DataTypes dt;
        if (type == Object.class) dt = DataTypes.type(data[pos]);
        else if (type instanceof Class<?>) dt = DataTypes.type((Class<?>) type);

        else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            type = parameterizedType.getRawType();
            dt = DataTypes.type((Class<?>) type);

        } else throw new Exception("error type");

        if (dt == DataTypes.OBJECT) {
            Class<?> cls = (Class<?>) type;
            if (cls == Object.class) return new SimpleToken(data, pos);


            Object o = cls.newInstance();
            int start = pos + 5, end = pos + len;
            for (int i = start; i < end; ) {
                Token token = new SimpleToken(data, i);
                Field field = cls.getDeclaredField(token.toObject(String.class));

                token = new SimpleToken(data, i += token.length());

                try {
                    Method set = cls.getDeclaredMethod(set_method_name(field.getName()), field.getType());
                    if (set != null) {
                        set.invoke(o, (Object) token.toObject(field.getType()));
                    }
                } catch (NoSuchMethodException ignored) {
                }
                i += token.length();
            }
            return o;

        } else
            return ((AbstractInterpreter) factory.get(dt)).toObject(type, data, pos, len);

    }


    private static String get_method_name(String filedName) throws Exception {
        return build_method_name("get", filedName);
    }

    private static String set_method_name(String filedName) {
        return build_method_name("set", filedName);
    }

    private static String build_method_name(String prefix, String filedName) {
        assert filedName != null;
        if (prefix != null || !"".equals(prefix.trim())) {
            byte[] items = filedName.getBytes();
            items[0] = (byte) ((char) items[0] - 'a' + 'A');
            return prefix + new String(items);
        }

        return filedName;
    }
}
