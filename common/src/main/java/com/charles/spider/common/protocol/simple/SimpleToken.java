package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.*;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LQ on 2015/11/10.
 * Update by LQ on 2017/4/17
 */
public class SimpleToken implements Token {
    private final static InterpreterFactory INTERPRETER_FACTORY = new InterpreterFactory(SimpleProtocol.instance());

    private byte[] data = null;
    private int pos = 0;

    public SimpleToken(byte[] data, int pos) {
        this.data = data;
        this.pos = pos;
    }



    private boolean isArray(){ return (data[pos]&0x80)>0;}


    @Override
    public DataTypes type() { return DataTypes.type((byte) (data[pos]&0x7F)); }

    @Override
    public int toInt() throws Exception {
        return (int) safe_interpreter_factory(String.class).unpack(String.class,data,pos,length());
    }

    @Override
    public byte toByte() throws Exception {
        return (byte) safe_interpreter_factory(String.class).unpack(String.class,data,pos,length());
    }

    @Override
    public float toFloat() throws Exception {
        return (float) safe_interpreter_factory(String.class).unpack(String.class,data,pos,length());
    }

    @Override
    public double toDouble() throws Exception {
        return (double) safe_interpreter_factory(String.class).unpack(String.class,data,pos,length());
    }

    @Override
    public char toChar() throws Exception {
        return (char) safe_interpreter_factory(String.class).unpack(String.class,data,pos,length());
    }

    @Override
    public long toLong() throws Exception {
        return (long) safe_interpreter_factory(String.class).unpack(String.class,data,pos,length());
    }

    @Override
    public boolean toBoolean() throws Exception {
        return (boolean) safe_interpreter_factory(String.class).unpack(String.class,data,pos,length());
    }

    @Override
    public String toString(Charset charset) throws Exception {
        return (String) safe_interpreter_factory(String.class).unpack(String.class,data,pos,length());
    }

    @Override
    public <T> T toClass(Class<?> cls) throws Exception {

        if(type()==DataTypes.NULL) return (T) INTERPRETER_FACTORY.get(DataTypes.NULL).unpack(cls,data,pos,length());


        if (cls == null) cls = Object.class;
        if (cls != Object.class && cls.isArray() != isArray()) throw new Exception("error type");

        Interpreter interpreter;
        if (cls == Object.class)
            interpreter = INTERPRETER_FACTORY.get(type());
        else if (DataTypes.type(cls) != type())
            throw new Exception("error type");
        else
            interpreter = INTERPRETER_FACTORY.get(type());

        return (T) interpreter.unpack(cls, data, pos, length());

    }


    @Override
    public <T> T[] toArray(Class<T> cls) throws Exception {
        if (type() != DataTypes.ARRAY) throw new Exception("error type");
        DataTypes t = DataTypes.type(cls);
        DataTypes at = DataTypes.type(data[pos + 6]);
        if (t == null) t = at;
        if (t != at) throw new Exception("error type");

        return null;
    }

    @Override
    public boolean isVaild() {
        return type()!=null;
    }

    @Override
    public int length() {

        if (!isArray()&&type().size() > -1) return type().size()+1;

        if (data.length - pos < 5) return -1;

        return ByteBuffer.wrap(data, pos + 1, 4).getInt()+5;
    }

    private Interpreter safe_interpreter_factory(Class<?> cls) throws Exception {
        DataTypes t = DataTypes.type(cls);
        if (cls==null ||t != type()) {
            throw new UnSupportTypeException(cls);
        }
        if (length()<0)
            throw new Exception("length not enough");

        return INTERPRETER_FACTORY.get(t);
    }



}
