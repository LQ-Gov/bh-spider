package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;
import com.charles.spider.common.protocol.ProtocolObject;
import com.charles.spider.common.protocol.Token;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

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
        return new String(buffer.put(data, pos + 1, buffer.limit()).array(), charset);
    }

    @Override
    public <T> T toClass(Class<T> cls) throws Exception {
        if(type==null) throw new Exception("error type");
        if(type==DataTypes.NULL) return null;

        if(cls.isPrimitive()) {
            if (Integer.class.equals(cls))
                return (T) Integer.valueOf(toInt());
            if(Byte.class.equals(cls))
                return (T) Byte.valueOf(toByte());
            if(Float.class.equals(cls))
                return (T) Float.valueOf(toFloat());
            if(Double.class.equals(cls))
                return (T) Double.valueOf(toDouble());
            if(Character.class.equals(cls))
                return (T) Character.valueOf(toChar());
            if(Long.class.equals(cls))
                return (T) Long.valueOf(toLong());
            if(Boolean.class.equals(cls))
                return (T) Boolean.valueOf(toBoolean());
        }
        if(String.class.equals(cls))
            return (T) toString(Charset.defaultCharset());

        T o = cls.newInstance();
        if(ProtocolObject.class.isAssignableFrom(cls)) {
            ProtocolObject obj = (ProtocolObject) o;
            obj.fromBytes(data, pos+1);
            return o;
        }

        int start = pos+5,end = start+length();

        for(int i=start;i<end;) {
            Token token = new SimpleToken(data, i);
            Field field = o.getClass().getField(token.toString(Charset.defaultCharset()));
            token = new SimpleToken(data, i += token.length());
            field.set(o, token.toClass(field.getType()));
            i += token.length();
        }

        return o;
    }

    @Override
    public <T> T[] toArray(Class<T> cls) throws Exception {

        List<T> list = new ArrayList<>();
        int start = pos + 5, end = pos + length();
        for (int i = start; i < end; ) {
            Token token = new SimpleToken(data, i);
            list.add(token.toClass(cls));
            i += token.length();
        }

        T[] result = (T[]) Array.newInstance(cls, list.size());
        list.toArray(result);
        return result;
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
