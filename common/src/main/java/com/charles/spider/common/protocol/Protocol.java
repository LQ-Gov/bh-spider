package com.charles.spider.common.protocol;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by LQ on 2015/10/20.
 */
public interface Protocol {
//    byte[] pack(int data);
//
//    byte[] pack(boolean data);
//
//    byte[] pack(float data);
//
//    byte[] pack(double data);
//
//    byte[] pack(long data);
//
//    byte[] pack(char data);
//
//    byte[] pack(byte data);
//
//    byte[] pack(String data) throws Exception;
//
//    byte[] pack(String data, Charset charset) throws Exception;

    <T> byte[] pack(T data) throws Exception;

//    <T> byte[] packArray(T[] data) throws Exception;

    Assemble assemble(byte[] data,int pos,int len) throws Exception;
}
