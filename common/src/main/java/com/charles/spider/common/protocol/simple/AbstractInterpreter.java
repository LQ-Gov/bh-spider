package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;
import org.apache.commons.lang3.ArrayUtils;

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
public abstract class AbstractInterpreter<T> implements Interpreter<T> {

    protected boolean support(Class c, Class... cls) {
        return c != null && ArrayUtils.indexOf(cls, c) > 0;
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

        if (support(cls)) return fromObject((T) input);


        if (cls.isArray() && support(cls.getComponentType())) return fromArray((T[]) input);

        if (Collection.class.isInstance(input)) {
            Type t = cls.getGenericSuperclass();
            Class componentType = (Class) ((ParameterizedType) t).getActualTypeArguments()[0];
            if (support(componentType))
                return fromCollection((Collection) input);
        }

        throw new Exception("type error");
    }

    public  T unpack(Class<T> cls, byte[] data, int pos, int len) throws Exception {
        if (support(cls)) return toObject(cls, data, pos, len);

        if (cls.isArray() && support(cls.getComponentType())) return (T) toArray(cls,data, pos, len);

        if (Collection.class.isAssignableFrom(cls)) {
            Collection<T> collection;
            if (Modifier.isAbstract(cls.getModifiers()) || Modifier.isInterface(cls.getModifiers()))
                collection = new ArrayList<T>();
            else collection = (Collection<T>) cls.newInstance();
            toCollection(cls,collection, data, pos, len);
            return (T) collection;
        }

        throw new Exception("type error");
    }




}
