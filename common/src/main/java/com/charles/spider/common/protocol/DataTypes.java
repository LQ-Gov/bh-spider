package com.charles.spider.common.protocol;

/**
 * Created by LQ on 2015/10/22.
 */
public enum DataTypes {

    NULL((byte)0,0),
    INT((byte)32,4),
    CHAR((byte)16,3),
    BOOL((byte)'B',1),
    FLOAT((byte)'F',4),
    DOUBLE((byte)'D',8),
    BYTE((byte)8,1),
    LONG((byte)64,8),
    CLASS((byte)'C',-1),
    ARRAY((byte)'A',-1),
    STRING((byte)'S',-1);


    private byte flag;
    private int size;
    DataTypes(byte flag,int size) {
        this.flag = flag; this.size=size;
    }

    public byte value()
    {
        return flag;
    }

    public int size(){return size;}

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
}
