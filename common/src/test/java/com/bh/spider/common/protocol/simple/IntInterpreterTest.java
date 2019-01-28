package com.bh.spider.common.protocol.simple;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Created by lq on 17-5-6.
 */
public class IntInterpreterTest extends AbstractInterpreterTest<IntInterpreter> {
    private IntInterpreter interpreter = new IntInterpreter();


    @Test
    public void support() throws Exception {
        Assertions.assertTrue(interpreter.support(Integer.class));
        Assertions.assertTrue(interpreter.support(int.class));
        Assertions.assertFalse(interpreter.support(Byte.class));
    }

    @Test
    public void fromArray() throws Exception {
        super.fromArray(new Integer[]{1,2,3});
    }

    @Test
    public void fromCollection() throws Exception {
        fromCollection(Arrays.asList(1,2,3));
    }

    @Test
    public void fromObject() throws Exception {

    }

}