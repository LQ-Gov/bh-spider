package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lq on 17-4-27.
 */
@SuppressWarnings("unchecked")
abstract class AbstractInterpreter<T> implements Interpreter<T> {
    protected final static int ARRAY_HEAD_LEN = 5;

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
        ByteBuffer buffer = ByteBuffer.allocate(len + 5).put(type.value()).putInt(len);
        data.forEach(buffer::put);
        return buffer.array();
    }

    protected abstract byte[] fromArray(T[] input) throws Exception;


    protected abstract byte[] fromCollection(Collection<T> collection) throws Exception;

    protected abstract byte[] fromObject(T o) throws Exception;


    protected abstract T[] toArray(Class<T> cls, byte[] data, int pos, int len) throws Exception;

    protected abstract void toCollection(Class<T> cls, Collection<T> collection, byte[] data, int pos, int len) throws Exception;


    protected abstract T toObject(Class<T> cls, byte[] data, int pos, int len) throws Exception;

    private boolean checkArrayVaild(byte[] data,int pos,int len) throws Exception {
        if ((data[pos] & 0x80) == 0) throw new Exception("not a array");//验证是否数组
        if (len < ARRAY_HEAD_LEN) throw new Exception("len error");//验证长度

        if (ByteBuffer.wrap(data, pos + 1, 4).getInt() != len - ARRAY_HEAD_LEN)
            throw new Exception("error len");//验证长度
        return true;

    }





    public byte[] pack(Object input) throws Exception {
        if(input==null) return fromObject((T) input);

        Class<?> cls = input.getClass();

        if (cls.isArray() && support(cls.getComponentType())) {
            if (cls.getComponentType().isPrimitive()) {
                int len = Array.getLength(input);

                Class refCls = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];

                T[] reference = (T[]) Array.newInstance(refCls, len);

                for (int i = 0; i < len; i++)
                    Array.set(reference, i, Array.get(input, i));
                return fromArray(reference);
            }
            return fromArray((T[]) input);
        }

        if (Collection.class.isInstance(input)) {
            Type t = cls.getGenericSuperclass();
            Class componentType = (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
            if (support(componentType))
                return fromCollection((Collection) input);
            else throw new Exception("not support type");
        }

        if (support(cls)) return fromObject((T) input);

        throw new Exception("type error");
    }

    public T unpack(Class<T> cls, byte[] data, int pos, int len) throws Exception {
        if (cls == null) cls = (Class<T>) Object.class;

        if ((cls==Object.class|| support(cls))&& (data[pos]&0x80)==0)
            return toObject(cls, data, pos, len);//如果直接支持，则进行toObject

        if((cls.isArray()||cls==Object.class)&&(data[pos]&0x80)>0&&support(cls.getComponentType()))
            return (T) toArray(cls,data,pos+ARRAY_HEAD_LEN,len-ARRAY_HEAD_LEN);

        if (Collection.class.isAssignableFrom(cls)&&(data[pos]&0x80)>0) {
            Collection<T> collection;
            if (Modifier.isAbstract(cls.getModifiers()) || Modifier.isInterface(cls.getModifiers()))
                collection = new ArrayList<>();
            else collection = (Collection<T>) cls.newInstance();
            toCollection(cls, collection, data, pos+ARRAY_HEAD_LEN, len-ARRAY_HEAD_LEN);
            return (T) collection;
        }

        throw new Exception("type error");
    }


}
