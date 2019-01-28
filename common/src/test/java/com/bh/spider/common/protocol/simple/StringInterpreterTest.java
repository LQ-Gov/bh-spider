package com.bh.spider.common.protocol.simple;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Created by lq on 17-5-6.
 */
public class StringInterpreterTest extends AbstractInterpreterTest<StringInterpreter> {
    @Test
    public void support() throws Exception {
        Assertions.assertTrue(interpreter().support(String.class));

    }

    @Test
    public void fromArray() throws Exception {
        fromArray(new String[]{null,null});
        fromArray(new String[]{"abcd","ddd",null,"bc"});
    }

    @Test
    public void fromCollection() throws Exception {
        fromCollection(Arrays.asList("3335","wcx","3",null));
    }

    @Test
    public void fromObject() throws Exception {
        fromObject(null);
        fromObject("11111");
        fromObject("");
    }

}