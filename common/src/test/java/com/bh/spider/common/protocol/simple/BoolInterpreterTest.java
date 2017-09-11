package com.bh.spider.common.protocol.simple;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by lq on 17-5-6.
 */
public class BoolInterpreterTest extends AbstractInterpreterTest<BoolInterpreter> {
    @Test
    public void support() throws Exception {
        Assert.assertTrue(interpreter().support(Boolean.class));
        Assert.assertTrue(interpreter().support(boolean.class));
        Assert.assertFalse(interpreter().support(Integer.class));
    }

    @Test
    public void fromArray() throws Exception {
        fromArray(new Boolean[]{});
        fromArray(new Boolean[]{true,false,true,false});//<8
        fromArray(new Boolean[]{true,false,true,false,true,true,true,false});//==8
        fromArray(new Boolean[]{true,false,true,true,false,false,false,false,true,true,true});//8<len<16
        fromArray(new Boolean[]{true,false,true,true,false,false,false,true,true,false,true,true,false,false,false,true});//==16


    }

    @Test
    public void fromCollection() throws Exception {
        fromCollection(Arrays.asList());
        fromCollection(Arrays.asList(true,false,true,false));//<8
        fromCollection(Arrays.asList(true,false,true,false,true,true,true,false));//==8
        fromCollection(Arrays.asList(true,false,true,true,false,false,false,false,true,true,true));//8<len<16
        fromCollection(Arrays.asList(true,false,true,true,false,false,false,true,true,false,true,true,false,false,false,true));//==16
    }

    @Test
    public void fromObject() throws Exception {
        fromObject(true);
        fromObject(false);
    }

}