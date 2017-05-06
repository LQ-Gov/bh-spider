package com.charles.spider.common.protocol.simple;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by lq on 17-5-6.
 */
public class CharInterpreterTest extends AbstractInterpreterTest<CharInterpreter> {
    @Test
    public void support() throws Exception {
        Assert.assertTrue(interpreter().support(Character.class));
        Assert.assertTrue(interpreter().support(char.class));
        Assert.assertFalse(interpreter().support(Integer.class));
    }

    @Test
    public void fromArray() throws Exception {
        fromArray(new Character[]{'a','A','C','F'});
    }

    @Test
    public void fromCollection() throws Exception {
        fromCollection(Arrays.asList('c','w','w','T'));
    }

    @Test
    public void fromObject() throws Exception {
        super.fromObject('E');
    }

}