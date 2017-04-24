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
    private byte[] data = null;
    private int pos = 0;
    private DataTypes type= null;
    private int len =-1;


    public SimpleToken(byte[] data, int pos) {
        this.data = data;
        this.pos = pos;
        type = DataTypes.type(data[pos]);

    }


    @Override
    public DataTypes type() {
        return type;
    }

    @Override
    public int toInt() throws Exception {
        return safe_build_buffer(DataTypes.INT).put(data,pos+1,type.size()).getInt();
    }

    @Override
    public byte toByte() throws Exception {
        return safe_build_buffer(DataTypes.BYTE).put(data,pos+1,type.size()).get();
    }

    @Override
    public float toFloat() throws Exception {
        return safe_build_buffer(DataTypes.FLOAT).put(data,pos+1,type.size()).getFloat();
    }

    @Override
    public double toDouble() throws Exception {
        return safe_build_buffer(DataTypes.DOUBLE).put(data,pos+1,type.size()).getDouble();
    }

    @Override
    public char toChar() throws Exception {
        return safe_build_buffer(DataTypes.CHAR).put(data,pos+1,type.size()).getChar();
    }

    @Override
    public long toLong() throws Exception {
        return safe_build_buffer(DataTypes.LONG).put(data,pos+1,type.size()).getLong();
    }

    @Override
    public boolean toBoolean() throws Exception {
        return safe_build_buffer(DataTypes.BOOL).put(data, pos + 1, type.size()).get() > 0;
    }

    @Override
    public String toString(Charset charset) throws Exception {
        ByteBuffer buffer = safe_build_buffer(DataTypes.STRING);
        return new String(buffer.put(data, pos + 5, buffer.limit()).array(), charset);
    }

    @Override
    public <T> T toClass(Class<T> cls) throws Exception {
        DataTypes t = DataTypes.type(cls);
        if(t==null) type = t;
        else if(t!=type) throw new Exception("error type");

        switch (t) {
            case BYTE:
                return (T) Byte.valueOf(toByte());
            case BOOL:
                return (T) Boolean.valueOf(toBoolean());
            case CHAR:
                return (T) Character.valueOf(toChar());
            case INT:
                return (T) Integer.valueOf(toInt());
            case LONG:
                return (T) Long.valueOf(toLong());
            case FLOAT:
                return (T) Float.valueOf(toFloat());
            case DOUBLE:
                return (T) Double.valueOf(toDouble());
            case STRING:
                return (T) toString(Charset.defaultCharset());
            case ARRAY:
                return (T) toArray(cls);
            case CLASS: {
                if (cls == null || cls == Object.class) return (T) this;
                T o = null;
                if (ProtocolObject.class.isAssignableFrom(cls)) {
                    o = cls.getConstructor(byte[].class, int.class, int.class).newInstance(data, pos + 5, len - 5);
                    ProtocolObject obj = (ProtocolObject) o;

                    return o;
                }
                o = cls.newInstance();
                int start = pos + 5, end = start + length();
                for (int i = start; i < end; ) {
                    Token token = new SimpleToken(data, i);
                    Field field = o.getClass().getField(token.toString(Charset.defaultCharset()));
                    token = new SimpleToken(data, i += token.length());
                    field.set(o, token.toClass(field.getType()));
                    i += token.length();
                }
                return o;
            }
            default:
                throw new Exception("error type");
        }
    }


    @Override
    public <T> T[] toArray(Class<T> cls) throws Exception {
        if (type != DataTypes.ARRAY) throw new Exception("error type");
        DataTypes t = DataTypes.type(cls);
        DataTypes at = DataTypes.type(data[pos + 6]);
        if (t == null) t = at;
        if (t != at) throw new Exception("error type");

        return (T[]) ArrayObjectFactory.get(t).write(cls, data, pos + 6, length() - 6).toObject();
    }

    @Override
    public boolean isVaild() {
        return type!=null;
    }

    @Override
    public int length() {
        if (type.size() > -1) return type.size()+1;

        if (data.length - pos < 5) return 0;

        return ByteBuffer.wrap(data, pos + 1, 4).getInt()+1;
    }

    private ByteBuffer safe_build_buffer(DataTypes t) throws Exception {
        if (t != type||t==DataTypes.NULL)
            throw new Exception("not a valid " + t.toString() + " data");
        if (t.size() > 0)
            return ByteBuffer.allocate(t.size());
        if (data.length - pos < 5)
            throw new Exception("length not enough");
        return ByteBuffer.allocate(length()-1);
    }



}
