package com.bh.spider.common.protocol.simple;

import com.bh.spider.common.protocol.DataTypes;
import com.bh.spider.common.protocol.UnSupportTypeException;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

/**
 * Created by lq on 17-4-27.
 */
@SuppressWarnings("unchecked")
abstract class AbstractInterpreter<T> implements Interpreter<T> {
    protected final static int ARRAY_HEAD_LEN = 6;

    protected boolean support(Class c, Class... cls) {
        if (c != null && cls != null) {
            for (Class it : cls) {
                if (it.isAssignableFrom(c))
                    return true;
            }

        }
        return false;
    }

    protected byte[] toBytes(DataTypes type, List<byte[]> data, int len) {
        ByteBuffer buffer = ByteBuffer.allocate(len + ARRAY_HEAD_LEN).put(type.value()).putInt(len);
        data.forEach(buffer::put);
        return buffer.array();
    }

    protected abstract byte[] fromArray(T[] input) throws Exception;


    protected abstract byte[] fromCollection(Collection<T> collection) throws Exception;

    protected abstract byte[] fromObject(T o) throws Exception;


    protected abstract T[] toArray(Type cls, byte[] data, int pos, int len) throws Exception;

    protected abstract void toCollection(Type componentType, Collection<T> collection, byte[] data, int pos, int len) throws Exception;


    protected abstract T toObject(Type type, byte[] data, int pos, int len) throws Exception;

//    private boolean checkArrayVaild(byte[] data,int pos,int len) throws Exception {
//        if ((data[pos] & 0x80) == 0) throw new Exception("not a array");//验证是否数组
//        if (len < ARRAY_HEAD_LEN) throw new Exception("len error");//验证长度
//
//        if (ByteBuffer.wrap(data, pos + 1, 4).getInt() != len - ARRAY_HEAD_LEN)
//            throw new Exception("error len");//验证长度
//        return true;
//
//    }


    public byte[] pack(Object input) throws Exception {
        if (input == null) return fromObject(null);

        Class<?> cls = input.getClass();


        if (!support(cls)) throw new UnSupportTypeException(input.getClass());


        byte[] data;


//        if (cls.isArray() && support(cls.getComponentType())) {
//            if (cls.getComponentType().isPrimitive()) {
//                int len = Array.getLength(input);
//
//                Class refCls = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//
//                T[] reference = (T[]) Array.newInstance(refCls, len);
//
//                for (int i = 0; i < len; i++)
//                    Array.set(reference, i, Array.get(input, i));
//                data = fromArray(reference);
//            } else
//                data = fromArray((T[]) input);
//            return data;
//        }
//
//        if (Collection.class.isInstance(input)) {
//            Type t = cls.getGenericSuperclass();
//            if (support(t)) return fromObject((T) input);
//
//            t = ((ParameterizedType) t).getActualTypeArguments()[0];
//            if (t instanceof Class<?> && support(t))
//                return fromCollection((Collection<T>) input);
//            else {
//                throw new UnSupportTypeException(t.getTypeName());
//            }
//        }


        if (support(cls)) return fromObject((T) input);

        throw new UnSupportTypeException(cls);
    }

    public T unpack(Type type, byte[] data, int pos, int len) throws Exception {
        if (type == null) type = Object.class;
        if (type == Object.class) return toObject(type, data, pos, len);

        if (support(type)) {
            return toObject(type, data, pos, len);
        }

        throw new UnSupportTypeException(type.getTypeName());
    }


}
