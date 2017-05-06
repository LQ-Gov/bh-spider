package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.Assemble;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by lq on 17-5-6.
 */
public class IntInterpreterTest {
    private IntInterpreter interpreter = new IntInterpreter();


    @Test
    public void support() throws Exception {
        Assert.assertTrue(interpreter.support(Integer.class));
        Assert.assertTrue(interpreter.support(int.class));
        Assert.assertFalse(interpreter.support(Byte.class));
    }

    @Test
    public void fromArray() throws Exception {
        Integer[] base = new Integer[]{1,2,3};
        byte[] bytes = interpreter.fromArray(base);

        Assert.assertArrayEquals(interpreter.toArray(bytes,0,bytes.length),base);
    }

    @Test
    public void fromCollection() throws Exception {

        List<Integer> base = Arrays.asList(1,2,3);

        byte[] bytes = interpreter.fromCollection(base);

        List<Integer> data = new ArrayList<>();
        interpreter.toCollection(data,bytes,0,bytes.length);

        Assert.assertEquals(base,data);
    }

    @Test
    public void fromObject() throws Exception {

    }

}