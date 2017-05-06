package com.charles.spider.common.protocol;

import java.nio.charset.Charset;

/**
 * Created by LQ on 2015/10/20.
 */
public class ProtocolBase implements Protocol {

    @Override
    public <T> byte[] pack(T data) throws Exception {
        if (data instanceof ProtocolObject)
            return ((ProtocolObject) data).toBytes();
        return new byte[0];
    }


    @Override
    public Assemble assemble(byte[] data, int pos, int len) throws Exception {
        return null;
    }
}
