package com.charles.spider.common.protocol.simple;

import com.charles.spider.common.protocol.DataTypes;
import com.charles.spider.common.protocol.Token;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Array;
import java.net.SocketImpl;

import static org.junit.Assert.*;

/**
 * Created by lq on 17-5-25.
 */
public class SimpleProtocolTest {

    void test(Object o,Class<?> cls) throws Exception {
        byte[] data = SimpleProtocol.instance().pack(o);
        Token token = SimpleProtocol.instance().assemble(data, 0, data.length).next();

        Object out = token.toClass(cls);

        if (o!=null&& o.getClass().isArray()) {
            Assert.assertArrayEquals((Object[]) o, (Object[]) out);
        } else
            Assert.assertEquals(out, o);


    }
    void test(Object o) throws Exception {
        test(o,null);
    }

    @Test
    public void pack() throws Exception {
        test(null);
        test(10);
        test(true);
        test("ABCKEDD");
        test('C');
        test(13L);
        test(new Integer[]{1, 2, 3, -3, 0});
        test(new Boolean[]{true, false, false, true, true});
        test(new Object[]{new byte[]{1,2,3},true,"abc"});

        test(DataTypes.ARRAY,DataTypes.class);
    }

    @Test
    public void assemble() throws Exception {
    }

}