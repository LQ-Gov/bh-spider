package com.charles.spider.common.protocol;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

/**
 * Created by LQ on 2015/10/20.
 */
public interface Protocol {

    static Class constructorCollectionClass(Class<? extends Collection> outer, Class<? extends Object> inner) {

        return null;
    }

    byte[] pack(Object data) throws Exception;

    Assemble assemble(byte[] data, int pos, int len) throws Exception;
}
