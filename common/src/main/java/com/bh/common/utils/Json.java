package com.bh.common.utils;

import com.bh.spider.common.fetch.Request;
import com.bh.spider.common.fetch.impl.RequestImpl;
import com.bh.spider.common.rule.Rule;
import com.bh.spider.common.rule.SeleniumRule;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

import java.lang.reflect.Type;
import java.util.Collection;
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
        mapper.configure(MapperFeature.PROPAGATE_TRANSIENT_MARKER,true);
        mapper.registerSubtypes(Rule.class);
        mapper.registerSubtypes(SeleniumRule.class);


        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(Request.class, RequestImpl.class);
        SimpleModule module = new SimpleModule("custom-module");
        module.setAbstractTypes(resolver);

        mapper.registerModule(module);
    }


    public static ObjectMapper get() {
        return mapper;
    }

    public static JavaType constructType(Type type){
        return mapper.getTypeFactory().constructType(type);
    }

    public static MapType mapType(JavaType keyClass,JavaType valueClass){
        return mapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass);
    }

    public static MapType mapType(Class<?> keyClass, Class<?> valueClass) {
        return mapper.getTypeFactory().constructMapType(HashMap.class, keyClass, valueClass);
    }


    public static CollectionType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass){
        return mapper.getTypeFactory().constructCollectionType(collectionClass,elementClass);
    }
}
