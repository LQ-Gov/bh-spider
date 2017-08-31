package com.bh.spider.scheduler.event.token;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;

public class JacksonToken implements Token {
    private ObjectMapper mapper;
    private JsonParser parser;

    public JacksonToken(ObjectMapper mapper, JsonParser parser) {
        this.mapper = mapper;
        this.parser = parser;
    }


    @Override
    public <T> T toObject(Type cls) throws Exception {
        return mapper.readValue(parser, mapper.getTypeFactory().constructType(cls));
    }

    @Override
    public boolean isVaild() {
        return false;
    }

    @Override
    public int length() {
        return 0;
    }
}
