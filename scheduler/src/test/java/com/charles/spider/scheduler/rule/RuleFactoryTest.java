package com.charles.spider.scheduler.rule;

import com.alibaba.fastjson.JSON;
import com.charles.spider.common.entity.Module;
import com.charles.spider.common.entity.Rule;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.junit.Test;

import javax.lang.model.type.TypeVariable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by lq on 7/6/17.
 */
public class RuleFactoryTest {
    private List<Boolean> o = Arrays.asList(true,false,true,false);


    @Test
    public void save() throws Exception {


        String path = "src/test/resources/rule";

        RuleFactory factory = new RuleFactory(path);
        Rule rule = new Rule("save-test","www.sina.com","*******");
        rule.setPattern("pattern-demo");
        factory.save(rule);


    }

    @Test
    public void get() throws Exception {
        String path = RuleFactoryTest.class.getClassLoader().getResource("rule/www.baidu.com.json").getPath();
        RuleFactory factory = new RuleFactory(path);
        List<Rule> list = factory.get();

        String[] chains =list.get(0).extractor("200");
        System.out.println( Arrays.toString(chains));
    }

    @Test
    public void t() throws NoSuchFieldException {
        List<Boolean> o = Arrays.asList(true,false,true,false);

        //List<String> o = new ArrayList<String>();

        System.out.println( JSON.toJSONString(o));




        ParameterizedType parameterizedType = (ParameterizedType) o.getClass().getGenericSuperclass();




        //parameterizedType = (ParameterizedType) oField.getGenericType();

        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];

        if (actualTypeArgument instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) actualTypeArgument;
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length == 1) {
                actualTypeArgument = upperBounds[0];
            }
        }

        System.out.println(actualTypeArgument.getTypeName());

    }



}