package com.bh.spider.rule;

import com.bh.spider.transfer.Json;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class JsonTest {

    @Test
    public  void test() throws IOException {
        Object[] rules = new Object[]{new Rule(0),new SeleniumRule()};

        List<Rule> list = new LinkedList<>();
        list.add(new Rule(1));
        list.add(new Rule(2));
        String value = Json.get().writeValueAsString(rules);
        System.out.println(Json.get().writeValueAsString(list));
        System.out.println(value);


        Rule[] r = Json.get().readValue(value,Json.get().getTypeFactory().constructArrayType(Rule.class));

        int a =0;
    }
}
