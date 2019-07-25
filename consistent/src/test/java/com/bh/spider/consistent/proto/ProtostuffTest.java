package com.bh.spider.consistent.proto;

import com.bh.spider.consistent.raft.Message;
import com.bh.spider.consistent.raft.MessageType;
import com.bh.spider.consistent.raft.node.Node;
import com.bh.spider.consistent.raft.serialize.ProtoBufUtils;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

/**
 * @author liuqi19
 * @version test, 2019-07-17 16:55 liuqi19
 **/
public class ProtostuffTest {

    @Test
    public void test0(){

        String hostname = StringUtils.repeat('x',10);
        Node node = new Node(10,hostname,10);

        Schema<Node> schema = RuntimeSchema.getSchema(Node.class);

        // Re-use (manage) this buffer to avoid allocating on every serialization
        LinkedBuffer buffer = LinkedBuffer.allocate(256);
        byte[] data = ProtostuffIOUtil.toByteArray(node,schema,buffer);
        Node result = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data,result,schema);


        System.out.println("hostname:"+node.hostname());

    }


    @Test
    public void test1(){
        int id = 103;

        byte[] data = ProtoBufUtils.serialize(id);

        Message message = Message.create(MessageType.APP_RESP,2,5,103);


        int a =0;
    }


    public void test2(){
        byte[] bytes = new byte[4];


    }
}
