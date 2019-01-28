package com.bh.spider.common.protocol.simple;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * Created by lq on 17-5-6.
 */
public class CharInterpreterTest extends AbstractInterpreterTest<CharInterpreter> {
    @Test
    public void support() throws Exception {
        Assertions.assertTrue(interpreter().support(Character.class));
        Assertions.assertTrue(interpreter().support(char.class));
        Assertions.assertFalse(interpreter().support(Integer.class));
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