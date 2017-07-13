package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.*;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.*;

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
        if (cls.isArray()) {
            Class<?> componentType = o.getClass().getComponentType();

            Object reference = o;
            if (componentType.isPrimitive()) {
                int len = Array.getLength(o);
                componentType = ClassUtils.primitiveToWrapper(componentType);
                reference = Array.newInstance(componentType, len);

                for (int i = 0; i < len; i++)
                    Array.set(reference, i, Array.get(o, i));
            }
            DataTypes type = DataTypes.type(componentType);
            AbstractInterpreter interpreter = (AbstractInterpreter) interpreterFactory.get(type);
            return interpreter.fromArray((Object[]) reference);
        }
        if (Collection.class.isAssignableFrom(cls)) {
            Type t = cls.getGenericSuperclass();
            t = ((ParameterizedType) t).getActualTypeArguments()[0];

            DataTypes type = t instanceof Class<?> ? DataTypes.type((Class<?>) t) : DataTypes.OBJECT;

            AbstractInterpreter interpreter = (AbstractInterpreter) interpreterFactory.get(type);
            return interpreter.fromCollection((Collection) o);
        }

        if (true) throw new Exception("error type");


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
        boolean isCollection = false;
        DataTypes dt;
        AbstractInterpreter interpreter;
        if (type == Object.class) dt = DataTypes.type(data[pos + ARRAY_HEAD_LEN - 1]);

        else if (type instanceof Class<?>) {
            Class<?> cls = (Class<?>) type;
            if (cls.isArray()) dt = DataTypes.type(cls.getComponentType());
            else if (Collection.class.isAssignableFrom(cls)) {
                isCollection = true;
                ParameterizedType parameterizedType = (ParameterizedType) cls.getGenericSuperclass();
                Type t = parameterizedType.getActualTypeArguments()[0];
                dt = t instanceof Class<?> ? DataTypes.type((Class<?>) t) : DataTypes.OBJECT;
            }
            else throw new Exception("error type");

        } else if (type instanceof ParameterizedType) {
            ParameterizedType t = (ParameterizedType) type;
            Class<?> rawType = (Class<?>) t.getRawType();
            Type[] arguments = t.getActualTypeArguments();

            dt = DataTypes.type(arguments == null || arguments.length == 0 ? Object.class : (Class<?>) arguments[0]);
            type = rawType;
        } else throw new Exception("error type");


        interpreter = (AbstractInterpreter) interpreterFactory.get(dt);
        interpreter = new AbstractInterpreterWrapper(interpreter);
        if (!isCollection)
            return interpreter.toArray(type, data, pos, len);

        Class<?> cls = (Class<?>) type;
        Collection collection = cls.isInterface() ? new ArrayList() : (Collection) cls.newInstance();

        interpreter.toCollection(type, collection, data, pos, len);

        return collection;
    }

    class AbstractInterpreterWrapper extends AbstractInterpreter {

        private AbstractInterpreter interpreter;

        public AbstractInterpreterWrapper(AbstractInterpreter interpreter) {
            this.interpreter = interpreter;
        }

        @Override
        public boolean support(Type cls) {
            return this.interpreter.support(cls);
        }

        @Override
        protected byte[] fromArray(Object[] input) throws Exception {
            return this.interpreter.fromArray(input);
        }

        @Override
        protected byte[] fromCollection(Collection collection) throws Exception {
            return this.interpreter.fromCollection(collection);
        }

        @Override
        protected byte[] fromObject(Object o) throws Exception {
            return this.interpreter.fromObject(o);
        }

        @Override
        protected Object[] toArray(Type cls, byte[] data, int pos, int len) throws Exception {
            return this.interpreter.toArray(cls, data, pos + ARRAY_HEAD_LEN, len - ARRAY_HEAD_LEN);
        }

        @Override
        protected void toCollection(Type cls, Collection collection, byte[] data, int pos, int len) throws Exception {
            this.interpreter.toCollection(cls, collection, data, pos + ARRAY_HEAD_LEN, len - ARRAY_HEAD_LEN);
        }

        @Override
        protected Object toObject(Type type, byte[] data, int pos, int len) throws Exception {
            return this.interpreter.toObject(type, data, pos, len);
        }
    }
}


