package com.bh.spider.common.protocol.simple;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Created by lq on 17-5-6.
 */
public class DoubleInterpreterTest extends AbstractInterpreterTest<DoubleInterpreter> {
    @Test
    public void support() throws Exception {
        Assertions.assertTrue(interpreter().support(Double.class));
        Assertions.assertTrue(interpreter().support(double.class));
        Assertions.assertFalse(interpreter().support(Integer.class));
    }

    @Test
    public void fromArray() throws Exception {
        fromArray(new Double[]{33.5,355.2,-142.5,12.1,-44.6});
    }

    @Test
    public void fromCollection() throws Exception {
        fromCollection(Arrays.asList(35.6,33.0,-23.5,-63.3,31.064));
    }

    @Test
    public void fromObject() throws Exception {
        fromObject(33.5);
        fromObject(-3.6);
    }

}