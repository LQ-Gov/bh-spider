package com.bh.spider.common.protocol.simple;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Created by lq on 17-5-6.
 */
public class ByteInterpreterTest extends AbstractInterpreterTest<ByteInterpreter> {

    @Test
    public void support() throws Exception {
        Assertions.assertTrue(interpreter().support(Byte.class));
        Assertions.assertTrue(interpreter().support(byte.class));
        Assertions.assertFalse(interpreter().support(Integer.class));

    }

    @Test
    public void fromArray() throws Exception {
        super.fromArray(new Byte[]{1,2,3,4});
    }

    @Test
    public void fromCollection() throws Exception {

        super.fromCollection(Arrays.asList((byte)1,(byte)2,(byte)128));
    }

    @Test
    public void fromObject() throws Exception {

        fromObject((byte)122);
    }

}