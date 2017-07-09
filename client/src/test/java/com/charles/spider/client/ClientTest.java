package com.charles.spider.client;

import com.charles.spider.common.moudle.Description;
import com.charles.spider.common.moudle.ModuleType;
import com.charles.spider.common.protocol.ProtocolFactory;
import com.charles.spider.common.protocol.Token;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * Created by lq on 17-3-26.
 */
public class ClientTest {

    private Client client = null;

    @Before
    public void before() throws IOException, URISyntaxException {
        client = new Client("127.0.0.1:8033");
    }


    @Test
    public void submit() throws Exception {
        Description desc = new Description(ModuleType.JAR);
        client.module().submit("target/client-1.0-SNAPSHOT.jar", desc, true);
    }

    @Test
    public void test() throws Exception {
        Description dest = new Description(ModuleType.JAR);
        dest.setName("ABC");
        byte[] data = ProtocolFactory.get().pack(dest);

        Token token = ProtocolFactory.get().assemble(data, 0, data.length).next();

        Description result = token.toClass(Description.class);

        System.out.println(result.getName());

    }

}