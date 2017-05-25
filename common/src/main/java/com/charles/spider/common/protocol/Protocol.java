package com.charles.spider.common.protocol;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by LQ on 2015/10/20.
 */
public interface Protocol {


    <T> byte[] pack(T data) throws Exception;

    Assemble assemble(byte[] data,int pos,int len) throws Exception;
}
