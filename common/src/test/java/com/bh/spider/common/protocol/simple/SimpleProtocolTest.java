package com.bh.spider.common.protocol.simple;

import com.bh.spider.common.protocol.DataTypes;
import com.bh.spider.common.protocol.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Created by lq on 17-5-25.
 */
public class SimpleProtocolTest {

    void test(Object o,Class<?> cls) throws Exception {
        byte[] data = SimpleProtocol.instance().pack(o);
        Token token = SimpleProtocol.instance().assemble(data, 0, data.length).next();

        Object out = token.toObject(cls);

        if (o!=null&& o.getClass().isArray()) {
            Assertions.assertArrayEquals((Object[]) o, (Object[]) out);
        } else
            Assertions.assertEquals(out, o);


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