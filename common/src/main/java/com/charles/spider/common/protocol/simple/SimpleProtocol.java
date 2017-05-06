package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by LQ on 2015/10/20.
 */
public final class SimpleProtocol extends ProtocolBase {
    private final static int MAX_LEN = (Integer.MAX_VALUE - 5);

    private volatile static SimpleProtocol ins = new SimpleProtocol();

    private InterpreterFactory interpreterFactory = new InterpreterFactory(this);


    public static SimpleProtocol instance(){
        return ins;
    }


    @Override
    public byte[] pack(int data) {
        System.out.println("pack int");
        byte[] result = new byte[5];
        result[0] = DataTypes.INT.value();
        result[1] = (byte) (data & 0xff);
        result[2] = (byte) ((data >> 8) & 0xff);
        result[3] = (byte) ((data >> 16) & 0xff);
        result[4] = (byte) ((data >> 24) & 0xff);
        return result;
    }

    @Override
    public byte[] pack(boolean data) {

        byte[] result = new byte[2];
        result[0] = DataTypes.BOOL.value();
        result[1] = (byte) (data ? 1 : 0);
        return result;
    }

    @Override
    public byte[] pack(float data) {
        byte[] result = pack(Float.floatToIntBits(data));
        result[0] = DataTypes.FLOAT.value();
        return result;
    }

    @Override
    public byte[] pack(double data) {
        byte[] result = pack(Double.doubleToLongBits(data));
        result[0] = DataTypes.DOUBLE.value();
        return result;
    }

    @Override
    public byte[] pack(long data) {
        byte[] result = new byte[9];
        result[0] = DataTypes.LONG.value();

        for (int i = 0; i < 8; i++)
            result[i + 1] = (byte) ((data >> (i * 8)) & 0xff);

        return result;
    }

    @Override
    public byte[] pack(char data) {
        byte[] result = new byte[3];
        result[0] = DataTypes.CHAR.value();
        result[1] = (byte) (data & 0xff);
        result[2] = (byte) ((data >> 8) & 0xff);
        return result;

    }

    @Override
    public byte[] pack(byte data) {
        byte[] result = new byte[2];
        result[0] = DataTypes.BYTE.value();
        result[1] = data;
        return result;
    }

    @Override
    public byte[] pack(String input) throws Exception {
        return pack(input, Charset.defaultCharset());
    }

    //数据最大长度为 65531
    @Override
    public byte[] pack(String input, Charset charset) throws Exception {
        if (input != null && input.length() > MAX_LEN)
            throw new Exception("the string length must between 0 and " + MAX_LEN);


        if (input == null)
            return new byte[]{DataTypes.STRING.value(), 1, 0, 0, 0, 0};

        byte[] result = new byte[input.length() + 6];

        ByteBuffer buffer = ByteBuffer.allocate(input.length() + 5).put(DataTypes.STRING.value())
                .putInt(input.length())
                .put(input.getBytes(charset));

        return buffer.array();
    }

    /**
     * @param o
     * @param <T>
     * @return
     * @throws Exception
     */
    @Override
    public <T> byte[] pack(T o) throws Exception {
        Class cls = o.getClass();
        if (o.getClass().isArray()) cls = o.getClass().getComponentType();
        else if (Collection.class.isAssignableFrom(cls)) {
            ParameterizedType parameterizedType = (ParameterizedType) o.getClass().getGenericSuperclass();
            cls = (Class) parameterizedType.getActualTypeArguments()[0];
        }

        DataTypes t = DataTypes.type(cls);
        return interpreterFactory.get(t).pack(o);
    }






    @Override
    public Assemble assemble(byte[] data, int pos, int len) throws Exception {
        return new SimpleAssemble(data, pos, len);
    }
}
