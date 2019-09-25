package com.bh.spider.scheduler.seriealize;

import com.bh.common.utils.Json;
import com.bh.spider.common.member.Node;
import com.bh.spider.scheduler.cluster.actuator.NodeCollection;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @author liuqi19
 * @version JacksonTest, 2019/9/16 2:42 下午 liuqi19
 **/
public class JacksonTest {


    @Test
    public void test0() throws JsonProcessingException {
        List<Node> list = new LinkedList<>();

        list.add(new Node());

        NodeCollection collection = new NodeCollection(list);

        System.out.println(Json.get().writeValueAsString(collection));
    }
}
