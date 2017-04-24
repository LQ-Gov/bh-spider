package com.charles.spider.common.protocol;

import java.util.Arrays;

/**
 * Created by LQ on 2015/10/22.
 */
public enum DataTypes {

    NULL((byte)0,0,null),
    INT((byte) 32,4,int.class),
    CHAR((byte)16,3,char.class),
    BOOL((byte)'B',1,boolean.class),
    FLOAT((byte)'F',4,float.class),
    DOUBLE((byte)'D',8,double.class),
    BYTE((byte)8,1,byte.class),
    LONG((byte)64,8,long.class),
    CLASS((byte)'C',-1,Object.class),
    ARRAY((byte)'A',-1,Object[].class),
    STRING((byte)'S',-1,String.class);


    private Class<?> cls;
    private byte flag;
    private int size;
    DataTypes(byte flag,int size,Class<?> cls) {
        this.flag = flag;
        this.size=size;
        this.cls=cls;
    }

    public byte value()
    {
        return flag;
    }

    public int size(){return size;}

    public Class<?> cls(){return cls;}

    public static DataTypes type(byte val) {
        switch (val) {
            case 32:
                return INT;
            case 16:
                return CHAR;
            case 'B':
                return BOOL;
            case 'F':
                return FLOAT;
            case 'D':
                return DOUBLE;
            case 8:
                return BYTE;
            case 64:
                return LONG;
            case 'C':
                return CLASS;
            case 'A':
                return ARRAY;
            case 'S':
                return STRING;
            case 0:
                return NULL;
            default:
                return null;
        }
    }

    public static DataTypes type(Class<?> cls) {
        if (cls == null) return null;
        if (cls == Integer.class || cls == int.class) return DataTypes.INT;
        else if (cls == Byte.class || cls == byte.class) return BYTE;
        else if (cls == Short.class || cls == short.class || cls == Character.class || cls == char.class) return CHAR;
        else if (cls == Boolean.class || cls == boolean.class) return BOOL;
        else if (cls == Long.class || cls == long.class) return LONG;
        else if (cls == Float.class || cls == float.class) return FLOAT;
        else if (cls == Double.class || cls == double.class) return DOUBLE;
        else if (cls == String.class) return STRING;
        else if (cls.isArray()) return ARRAY;
        else if (!cls.isPrimitive()) return CLASS;
        else return NULL;
    }
}
