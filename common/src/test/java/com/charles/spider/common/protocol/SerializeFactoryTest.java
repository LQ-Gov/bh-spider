package com.charles.spider.common.protocol;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by lq on 17-5-18.
 */
public class SerializeFactoryTest {
    @Test
    public void serialize() throws Exception {
        byte[] data = SerializeFactory.serialize(1,true,"abc");
        Object[] result = SerializeFactory.deserialize(data,null);
    }

    @Test
    public void deserialize() throws Exception {
    }

}