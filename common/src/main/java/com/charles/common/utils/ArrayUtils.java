package com.charles.common.utils;

import org.apache.commons.lang3.ClassUtils;

/**
 * Created by lq on 17-5-31.
 */
public class ArrayUtils extends org.apache.commons.lang3.ArrayUtils {

    public static Object toPrimitive(final Object array){
        if (array == null) {
            return null;
        }
        Class<?> ct = array.getClass().getComponentType();
        Class<?> pt = ClassUtils.wrapperToPrimitive(ct);

        if(Byte.TYPE.equals(pt))
            return org.apache.commons.lang3.ArrayUtils.toPrimitive((Byte[])array);
        else
            return org.apache.commons.lang3.ArrayUtils.toPrimitive(array);
    }
}
