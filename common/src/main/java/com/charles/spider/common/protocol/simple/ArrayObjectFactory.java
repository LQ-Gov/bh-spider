package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;
import com.charles.spider.common.protocol.ProtocolObject;

/**
 * Created by lq on 17-4-22.
 */
public class ArrayObjectFactory {


    public static ProtocolObject get(DataTypes type){
        switch (type)
        {
            case BYTE:return new ByteArrayObject();
            case CLASS:return new ClassArrayObject();
        }
        return null;
    }
}
