package com.charles.spider.common.protocol;

/**
 * Created by LQ on 2015/10/20.
 */
public interface ProtocolObject<T> {

    //序列化接口
    ProtocolObject<T> write(T input);
    byte[] toBytes();

    //反序列化借口
    ProtocolObject<T> write(byte[] input,int pos ,int len);
    ProtocolObject<T> write(Class<?> cls,byte[] input,int pos,int len);
    T toObject();
}
