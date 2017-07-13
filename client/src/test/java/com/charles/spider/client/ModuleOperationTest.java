package com.charles.spider.client;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Before;
import org.junit.Test;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by lq on 7/10/17.
 */
public class ModuleOperationTest {

    Client client = null;

    @Before
    public void before() throws IOException, URISyntaxException {
        //client = new Client("127.0.0.1:8033");
    }


    @Test
    public void select() throws Exception {

         client.module().select();
    }

    @Test
    public void select1() throws Exception {
    }

    @Test
    public void t(){
        ParameterizedType type = ParameterizedTypeImpl.make(List.class,new Type[]{String.class},null);


        JSON.parseObject("[]",type);
        if(type instanceof List){
            int ww =0;
        }

        int a =0;
    }

}