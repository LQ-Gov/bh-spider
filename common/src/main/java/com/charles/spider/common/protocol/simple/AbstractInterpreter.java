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
public abstract class AbstractInterpreter<T> implements Interpreter<T> {

    protected boolean support(Class c, Class... cls) {
        if (c != null && cls != null) {
            for (Class it : cls) {
                if (it.isAssignableFrom(c))
                    return true;
            }

        }
        return false;
    }


    protected byte[] toBytes(DataTypes type, List<byte[]> data,int len) {
        ByteBuffer buffer = ByteBuffer.allocate(len + 5).put(type.value()).putInt(len);
        data.forEach(buffer::put);
        return buffer.array();
    }

    protected abstract byte[] fromArray(T[] input) throws Exception;


    protected abstract byte[] fromCollection(Collection<T> collection) throws Exception;

    protected abstract byte[] fromObject(T o) throws Exception;


    protected abstract T[] toArray(Class<T> cls, byte[] data,int pos,int len) throws Exception;

    protected abstract void toCollection(Class<T> cls, Collection<T> collection, byte[] data,int pos,int len) throws Exception;



    protected  abstract T toObject(Class<T> cls,byte[] data,int pos,int len) throws Exception;


    public byte[] pack(Object input) throws Exception {
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

    public  T unpack(Class<T> cls, byte[] data, int pos, int len) throws Exception {
        if (cls==null|| cls == Object.class|| support(cls)) return toObject(cls, data, pos, len);

        if (cls.isArray() && support(cls.getComponentType())) return (T) toArray(cls, data, pos, len);

        if (Collection.class.isAssignableFrom(cls)) {
            Collection<T> collection;
            if (Modifier.isAbstract(cls.getModifiers()) || Modifier.isInterface(cls.getModifiers()))
                collection = new ArrayList<T>();
            else collection = (Collection<T>) cls.newInstance();
            toCollection(cls, collection, data, pos, len);
            return (T) collection;
        }

        throw new Exception("type error");
    }




}
