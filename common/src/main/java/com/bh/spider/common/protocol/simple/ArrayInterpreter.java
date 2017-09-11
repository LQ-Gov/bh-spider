package com.bh.spider.common.protocol.simple;

import com.bh.spider.common.protocol.DataTypes;
import com.bh.spider.common.protocol.Protocol;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

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
    public boolean support(Type cls) {
        if (cls instanceof Class<?>)
            return ((Class) cls).isArray() || Collection.class.isAssignableFrom((Class<?>) cls);
        if (cls instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) cls).getRawType();
            return Collection.class.isAssignableFrom((Class<?>) rawType);
        }

        return false;
    }

    @Override
    protected byte[] fromArray(Object[] input) throws Exception {
        throw new RuntimeException("not support");

    }

    @Override
    protected byte[] fromCollection(Collection<Object> collection) throws Exception {

        throw new RuntimeException("not support");
    }

    @Override
    protected byte[] fromObject(Object o) throws Exception {

        Class<?> cls = o.getClass();

        if(TypeUtils.isArrayType(cls)) {

            if (!cls.isArray()) throw new Exception("error type");
            Type componentType = TypeUtils.getArrayComponentType(cls);

            cls = (Class<?>) componentType;

            if (cls.isPrimitive()) {
                int len = Array.getLength(o);
                Object array = Array.newInstance(ClassUtils.primitiveToWrapper(cls), len);

                for (int i = 0; i < len; i++) Array.set(array, i, Array.get(o, i));

                o = array;
            }

            DataTypes dt = DataTypes.type((Class<?>) componentType);

            AbstractInterpreter interpreter = (AbstractInterpreter) interpreterFactory.get(dt);

            return interpreter.fromArray((Object[]) o);
        }

        return interpreterFactory.get(DataTypes.COLLECTION).pack(o);
    }

    @Override
    protected Object[] toArray(Type cls, byte[] data, int pos, int len) throws Exception {
        //return new Object[0];
        throw new Exception("not support");
    }

    @Override
    protected void toCollection(Type cls, Collection<Object> collection, byte[] data, int pos, int len) throws Exception {
        throw new Exception("not support");
    }

    @Override
    protected Object toObject(Type type, byte[] data, int pos, int len) throws Exception {

        if (TypeUtils.isArrayType(type)) {

            Type componentType = TypeUtils.getArrayComponentType(type);

            DataTypes dt = componentType == null ? DataTypes.type(data[pos + 5]) : DataTypes.type((Class<?>) componentType);

            AbstractInterpreter interpreter = (AbstractInterpreter) interpreterFactory.get(dt);

            return interpreter.toArray(componentType, data, pos + ARRAY_HEAD_LEN, len - ARRAY_HEAD_LEN);
        }

        return interpreterFactory.get(DataTypes.COLLECTION).unpack(type, data, pos, len);

    }

}


