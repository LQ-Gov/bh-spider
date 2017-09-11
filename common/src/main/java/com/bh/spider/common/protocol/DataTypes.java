package com.bh.spider.common.protocol;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LQ on 2015/10/22.
 */
public enum DataTypes {
    NULL((byte)0,0),
    INT((byte) 32,4,Integer.class,int.class),
    CHAR((byte)16,2,Character.class,char.class),
    BOOL((byte)'B',1,Boolean.class,boolean.class),
    FLOAT((byte)'F',4,Float.class,float.class),
    DOUBLE((byte)'D',8,Double.class,double.class),
    BYTE((byte)8,1,Byte.class,byte.class),
    LONG((byte)64,8,Long.class,long.class),
    STRING((byte)'S',-1,String.class),
    OBJECT((byte)'O',-1),
    ARRAY((byte)'A',-1),
    COLLECTION((byte)'C',-1),
    ENUM((byte)'E',-1);


    private static Map<Byte,DataTypes> BYTE_VALUE_MAP = new HashMap<>();
    private static Map<Class,DataTypes> CLASS_VALUE_MAP= new HashMap<>();




    private byte flag;
    private int size;
    private Class[] cls;
    DataTypes(byte flag,int size,Class... cls) {
        this.flag = flag;
        this.size = size;
        this.cls = cls;
    }

    public byte value()
    {
        return flag;
    }

    public int size(){return size;}

    public static DataTypes type(byte val) {
        return BYTE_VALUE_MAP.get(val);
    }

    public static DataTypes type(Class<?> cls) {
        if (cls == null) return null;
        DataTypes value = CLASS_VALUE_MAP.get(cls);

        if(value!=null) return value;

        if(cls.isArray()) return ARRAY;

        if(Collection.class.isAssignableFrom(cls)) return COLLECTION;

        if(cls.isEnum()) return ENUM;

        if(!cls.isPrimitive()) return OBJECT;


        return NULL;
    }



    static {
        for (DataTypes it : DataTypes.values()) {
            BYTE_VALUE_MAP.put(it.value(), it);
            if (it.cls != null && it.cls.length > 0)
                Arrays.stream(it.cls).forEach(x -> CLASS_VALUE_MAP.put(x, it));
        }
    }
}
