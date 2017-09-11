package com.bh.spider.common.protocol.simple;

import com.bh.spider.common.protocol.DataTypes;
import com.bh.spider.common.protocol.Protocol;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by lq on 7/14/17.
 */
public class CollectionInterpreter extends AbstractInterpreter<Object> {

    private Protocol protocol;
    private InterpreterFactory factory;

    public CollectionInterpreter(Protocol protocol, InterpreterFactory factory) {
        this.protocol = protocol;
        this.factory = factory;
    }

    @Override
    public boolean support(Type cls) {
        Class<?> rawClass = TypeUtils.getRawType(cls,null);
        return rawClass!=null&&Collection.class.isAssignableFrom(rawClass);
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
        ParameterizedType parameterizedType = (ParameterizedType) o.getClass().getGenericSuperclass();
        Type type = parameterizedType.getActualTypeArguments()[0];

        DataTypes dt = type instanceof Class<?> ? DataTypes.type((Class<?>) type) : DataTypes.OBJECT;

        AbstractInterpreter interpreter = (AbstractInterpreter) factory.get(dt);

        return interpreter.fromCollection((Collection) o);
    }

    @Override
    protected Object[] toArray(Type cls, byte[] data, int pos, int len) throws Exception {
        throw new RuntimeException("not support");
    }

    @Override
    protected void toCollection(Type componentType, Collection<Object> collection, byte[] data, int pos, int len) throws Exception {
        throw new RuntimeException("not support");
    }

    @Override
    protected Object toObject(Type type, byte[] data, int pos, int len) throws Exception {


        Class<?> rawType = TypeUtils.getRawType(type, null);


        Map<TypeVariable<?>, Type> variables = TypeUtils.getTypeArguments(
                (ParameterizedType) (type instanceof Class<?> ? ((Class) type).getGenericSuperclass() : type));


        Type E = variables.values().iterator().next();

        DataTypes dt = type instanceof Class<?> ? DataTypes.type((Class<?>) type) : DataTypes.OBJECT;

        AbstractInterpreter interpreter = (AbstractInterpreter) factory.get(dt);

        Collection collection = rawType.isInterface() ? new ArrayList() : (Collection) rawType.newInstance();

        interpreter.toCollection(E, collection, data, pos + ARRAY_HEAD_LEN, len - ARRAY_HEAD_LEN);

        return collection;


    }
}
