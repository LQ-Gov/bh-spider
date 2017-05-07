package com.charles.spider.common.protocol.simple;

import java.util.Collection;

/**
 * Created by lq on 17-5-6.
 */
public abstract class UniqueInterpreter<T> extends AbstractInterpreter<T>  {

    protected abstract T toObject(byte[] data,int pos, int len) throws Exception;

    protected abstract T[] toArray(byte[] data,int pos,int len) throws  Exception;

    protected abstract void toCollection(Collection<T> collection, byte[] data, int pos, int len) throws Exception;

    @Override
    protected  T toObject(Class<T> cls, byte[] data, int pos, int len) throws Exception {
        return toObject(data,pos,len);
    }

    @Override
    protected T[] toArray(Class<T> cls, byte[] data, int pos, int len) throws Exception {
        return toArray(data,pos,len);
    }


    @Override
    protected void toCollection(Class<T> cls, Collection<T> collection, byte[] data, int pos, int len) throws Exception {
        toCollection(collection, data, pos, len);
    }


/**
 * 以下代码为了处理数组强制转换的情况时，值类型无法转换的情况，即AbstractInterpreter中，
 * if (cls.isArray() && support(cls.getComponentType())) {
 *   return fromArray((T[]) input);
 * }
 * 这部分代码异常,无法强制转换,如不重写以下部分，需要对数组进行一次便利，浪费资源,此时注释掉是为了根据实际情况考虑
 * update time:2017-5-6
 *
 *
 */
//    protected byte[] fromPrimitiveArray(Object input){
//        return new byte[0];
//    }
//
//    protected byte[] fromReferenceArray(T[] input){
//        return new byte[0];
//    }
//
//    @Override
//    protected byte[] fromArray(Object input) throws Exception {
//        Class<?> cls = input.getClass();
//        if (cls.isArray()) {
//            if (cls.getComponentType().isPrimitive()) return fromPrimitiveArray(input);
//            else return fromReferenceArray((T[]) input);
//        }
//        throw new Exception("error type");
//    }
}
