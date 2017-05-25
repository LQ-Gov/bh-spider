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
        return safe_build_buffer(DataTypes.INT).put(data,pos+1,type().size()).getInt();
    }

    @Override
    public byte toByte() throws Exception {
        return safe_build_buffer(DataTypes.BYTE).put(data,pos+1,type().size()).get();
    }

    @Override
    public float toFloat() throws Exception {
        return safe_build_buffer(DataTypes.FLOAT).put(data,pos+1,type().size()).getFloat();
    }

    @Override
    public double toDouble() throws Exception {
        return safe_build_buffer(DataTypes.DOUBLE).put(data,pos+1,type().size()).getDouble();
    }

    @Override
    public char toChar() throws Exception {
        return safe_build_buffer(DataTypes.CHAR).put(data,pos+1,type().size()).getChar();
    }

    @Override
    public long toLong() throws Exception {
        return safe_build_buffer(DataTypes.LONG).put(data,pos+1,type().size()).getLong();
    }

    @Override
    public boolean toBoolean() throws Exception {
        return safe_build_buffer(DataTypes.BOOL).put(data, pos + 1, type().size()).get() > 0;
    }

    @Override
    public String toString(Charset charset) throws Exception {
        ByteBuffer buffer = safe_build_buffer(DataTypes.STRING);
        return new String(buffer.put(data, pos + 5, buffer.limit()).array(), charset);
    }

    @Override
    public <T> T toClass(Class<?> cls) throws Exception {

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

    private ByteBuffer safe_build_buffer(DataTypes t) throws Exception {
        if (t != type()||t==DataTypes.NULL)
            throw new Exception("not a valid " + t.toString() + " data");
        if (t.size() > 0)
            return ByteBuffer.allocate(t.size());
        if (data.length - pos < 5)
            throw new Exception("length not enough");
        return ByteBuffer.allocate(length()-1);
    }



}
