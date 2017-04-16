package com.charles.spider.common.protocol;

import com.charles.spider.common.protocol.simple.SimpleProtocol;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by lq on 17-4-16.
 */
public class ProtocolTest {
    private Protocol protocol = null;

    @Before
    public void init(){
        protocol = new SimpleProtocol();
    }

    @Test
    public void pack() throws Exception {
        int input = 101;
        Integer input2 = 122;
        Object input3 = new Integer(133);
        protocol.pack(10);
        protocol.pack(input);
        protocol.pack(input2);
        protocol.pack(input3);
    }

}