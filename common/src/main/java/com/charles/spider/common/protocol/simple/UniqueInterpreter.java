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
}
