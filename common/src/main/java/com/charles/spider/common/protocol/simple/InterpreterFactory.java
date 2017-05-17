package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;
import com.charles.spider.common.protocol.Protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-5-6.
 */
public class InterpreterFactory {
    private final Map<DataTypes,Interpreter> INTERPRETERS = new HashMap<>();

    public InterpreterFactory(Protocol protocol) {
        INTERPRETERS.put(DataTypes.INT, new IntInterpreter());
        INTERPRETERS.put(DataTypes.BYTE, new ByteInterpreter());
        INTERPRETERS.put(DataTypes.BOOL, new BoolInterpreter());
        INTERPRETERS.put(DataTypes.CHAR, new CharInterpreter());
        INTERPRETERS.put(DataTypes.LONG, new LongInterpreter());
        INTERPRETERS.put(DataTypes.FLOAT, new FloatInterpreter());
        INTERPRETERS.put(DataTypes.DOUBLE, new DoubleInterpreter());
        INTERPRETERS.put(DataTypes.STRING, new StringInterpreter());
        INTERPRETERS.put(DataTypes.CLASS, new ClassInterpreter(protocol));
        INTERPRETERS.put(DataTypes.ARRAY, new ArrayInterpreter(protocol,this));
        INTERPRETERS.put(DataTypes.ENUM, new EnumInterpreter());
    }

    public Interpreter get(DataTypes type){
        return INTERPRETERS.get(type);
    }

}
