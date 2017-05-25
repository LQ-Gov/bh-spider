package com.charles.spider.common.protocol;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by lq on 17-5-18.
 */
public class SerializeFactoryTest {

    private void serializHelper(Object... input) throws Exception {
        byte[] data = SerializeFactory.serialize(input);
        Object[] result = SerializeFactory.deserialize(data,null);
        Assert.assertArrayEquals(input,result);
    }

    @Test
    public void serialize() throws Exception {
        serializHelper(1,true,"abc");
        serializHelper(null,null,null);
    }

    @Test
    public void deserialize() throws Exception {
    }

}