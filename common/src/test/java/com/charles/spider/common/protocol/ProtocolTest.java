package com.charles.spider.common.protocol;

import com.charles.spider.common.protocol.simple.SimpleProtocol;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by lq on 17-4-16.
 */
public class ProtocolTest {
    private Protocol protocol = null;

    public <T> void cast(T o){}

    public  void cast(Object[] o){}
   // public void cast(byte[] o){}

    @Before
    public void init(){
        protocol = new SimpleProtocol();
    }

    @Test
    public void pack_init() throws Exception {
        Byte[] data = new Byte[3];
        Object o = data;

        String s ;
        //protocol.pack(s);
        cast(o);
    }

}