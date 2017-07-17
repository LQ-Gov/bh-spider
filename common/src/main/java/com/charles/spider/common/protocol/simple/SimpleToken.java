package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.*;
import org.apache.commons.lang3.reflect.TypeUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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


    private boolean isArray() {
        return (data[pos] & 0x80) > 0;
    }


    @Override
    public DataTypes type() {
        return DataTypes.type((byte) (data[pos] & 0x7F));
    }


    @Override
    public <T> T toObject(Type cls) throws Exception {

        Interpreter interpreter;

        if (cls == null) cls = Object.class;

        if (type() == DataTypes.NULL) interpreter = INTERPRETER_FACTORY.get(DataTypes.NULL);

        else if (cls == Object.class) interpreter = INTERPRETER_FACTORY.get(type());

        Class<?> rawClass = TypeUtils.getRawType(cls, null);

        interpreter = INTERPRETER_FACTORY.get(DataTypes.type(rawClass));



        return (T) interpreter.unpack(cls, data, pos, length());
    }


    @Override
    public boolean isVaild() {
        return type() != null;
    }

    @Override
    public int length() {

        if (!isArray() && type().size() > -1) return type().size() + 1;

        if (data.length - pos < 5) return -1;

        return ByteBuffer.wrap(data, pos + 1, 4).getInt() + 5;
    }

    private Interpreter safe_interpreter_factory(Class<?> cls) throws Exception {
        DataTypes t = DataTypes.type(cls);
        if (cls == null || t != type()) {
            throw new UnSupportTypeException(cls);
        }
        if (length() < 0)
            throw new Exception("length not enough");

        return INTERPRETER_FACTORY.get(t);
    }


}
