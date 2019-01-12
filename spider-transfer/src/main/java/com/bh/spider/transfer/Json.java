package com.bh.spider.transfer;

import com.bh.spider.rule.Rule;
import com.bh.spider.rule.SeleniumRule;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.type.MapType;

import java.util.HashMap;

/**
 * Created by lq on 7/17/17.
 */
public class Json {
    private final static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        mapper.registerSubtypes(Rule.class);
        mapper.registerSubtypes(SeleniumRule.class);
    }


    public static ObjectMapper get() {
        return mapper;
    }


    public static MapType mapType(Class<?> keyClass, Class<?> valueClass) {
        return mapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass);
    }
}
