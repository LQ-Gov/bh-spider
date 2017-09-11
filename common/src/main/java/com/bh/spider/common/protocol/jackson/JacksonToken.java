package com.bh.spider.common.protocol.jackson;

import com.bh.spider.common.protocol.DataTypes;
import com.bh.spider.common.protocol.Token;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;

/**
 * Created by lq on 7/16/17.
 */
public class JacksonToken implements Token {
    private ObjectMapper mapper;
    private JsonParser parser;

    public JacksonToken(ObjectMapper mapper, JsonParser parser) {
        this.mapper = mapper;
        this.parser = parser;
    }

    @Override
    public DataTypes type() {
        return null;
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
