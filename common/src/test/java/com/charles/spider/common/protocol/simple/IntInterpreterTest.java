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
public class IntInterpreterTest extends AbstractInterpreterTest<IntInterpreter> {
    private IntInterpreter interpreter = new IntInterpreter();


    @Test
    public void support() throws Exception {
        Assert.assertTrue(interpreter.support(Integer.class));
        Assert.assertTrue(interpreter.support(int.class));
        Assert.assertFalse(interpreter.support(Byte.class));
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