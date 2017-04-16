package com.charles.spider.common.protocol;

/**
 * Created by LQ on 2015/10/20.
 */
public interface ProtocolObject {
    byte[] toBytes() throws Exception;
    void fromBytes(byte[] data,int pos) throws Exception;

    /**
     * 传输协议是否在当前类的自定义的数据前添加前缀,
     * 一般为true,
     * 除非自定义的数据协议符合所使用的协议格式
     * @return
     */
    boolean isAutoPrefix();

}
