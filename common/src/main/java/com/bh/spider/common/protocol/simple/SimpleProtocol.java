package com.bh.spider.common.protocol.simple;

import com.bh.spider.common.protocol.Assemble;
import com.bh.spider.common.protocol.DataTypes;
import com.bh.spider.common.protocol.Protocol;

/**
 * Created by LQ on 2015/10/20.
 */
public final class SimpleProtocol implements Protocol {
    private final static int MAX_LEN = (Integer.MAX_VALUE - 5);

    private volatile static SimpleProtocol ins = new SimpleProtocol();

    private InterpreterFactory interpreterFactory = new InterpreterFactory(this);


    public static SimpleProtocol instance() {
        return ins;
    }

    /**
     * pack data
     *
     * @param o
     * @return
     * @throws Exception
     */
    public byte[] pack(Object o) throws Exception {
        //null
        //基本类型

        //Array
        //Object
        if (o == null) return interpreterFactory.get(DataTypes.NULL).pack(null);


        DataTypes type;

        Class<?> cls = o.getClass();

        type = DataTypes.type(o.getClass());


        return interpreterFactory.get(type).pack(o);
    }


    @Override
    public Assemble assemble(byte[] data, int pos, int len) throws Exception {
        return new SimpleAssemble(data, pos, len);
    }
}
