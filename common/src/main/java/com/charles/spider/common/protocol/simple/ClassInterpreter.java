package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.Assemble;
import com.charles.spider.common.protocol.DataTypes;
import com.charles.spider.common.protocol.Protocol;
import com.charles.spider.common.protocol.Token;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lq on 17-5-5.
 */
public class ClassInterpreter extends AbstractInterpreter<Object> {
    private Protocol protocol = null;

    public ClassInterpreter(Protocol protocol) {
        this.protocol = protocol;
    }


    @Override
    public boolean support(Class cls) {
        return support(cls, Object.class);
    }

    @Override
    protected byte[] fromArray(Object[] input) throws Exception {
        return fromCollection(Arrays.asList(input));
    }

    @Override
    protected byte[] fromCollection(Collection<Object> collection) throws Exception {
        List<byte[]> store = new LinkedList<>();

        int len = 0;

        for (Object o : collection) {
            byte[] bytes = fromObject(o);
            len += bytes.length;
            store.add(bytes);
        }

        return toBytes(DataTypes.CLASS, store, len);
    }

    @Override
    protected byte[] fromObject(Object o) throws Exception {
        if(o==null) return protocol.pack(null);
        Class cls = o.getClass();

        DataTypes type = DataTypes.type(cls);
        if (type == DataTypes.CLASS) {
            Field[] fields = cls.getDeclaredFields();
            List<byte[]> list = new LinkedList<>();
            int len = 0;
            for (Field field : fields) {
                byte[] key = protocol.pack(field.getName());
                Method get = cls.getMethod(get_method_name(field.getName()));

                if (get != null) {
                    byte[] value = protocol.pack(get.invoke(o));
                    len += key.length + value.length;
                    list.add(key);
                    list.add(value);
                }
            }
            return toBytes(DataTypes.CLASS, list, len);

        } else return protocol.pack(o);
    }

    @Override
    protected Object[] toArray(Class<Object> cls, byte[] data, int pos, int len) throws Exception {
        List<Object> list = new LinkedList<>();
        toCollection(cls, list, data, pos, len);
        return list.toArray(new Object[list.size()]);
    }

    @Override
    protected void toCollection(Class<Object> cls, Collection<Object> collection, byte[] data, int pos, int len) throws Exception {
        Assemble assemble = protocol.assemble(data, pos, len);

        Token token;
        while ((token = assemble.next()) != null) {
            collection.add(token.toClass(cls));
        }
    }

    @Override
    protected Object toObject(Class<Object> cls, byte[] data, int pos, int len) throws Exception {
        DataTypes type = (cls == null || cls == Object.class) ?
                DataTypes.type(data[pos])
                : DataTypes.type(cls);

        if (type == DataTypes.CLASS) {
            if (cls == null || cls == Object.class) return new SimpleToken(data, pos);
            else {
                Object o = cls.newInstance();
                Token token = new SimpleToken(data, pos);
                int start = pos + 4, end = pos + token.length();
                for (int i = start; i < end; ) {
                    token = new SimpleToken(data, i);
                    Field field = cls.getField(token.toString(Charset.defaultCharset()));
                    token = new SimpleToken(data, i += token.length());
                    field.set(o, token.toClass(field.getType()));
                    i += token.length();
                }

                return o;
            }
        } else return protocol.assemble(data, pos, len).next().toClass(cls);
    }


    private static String get_method_name(String filedName) throws Exception {
        byte[] items = filedName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return "get" + new String(items);
    }
}
