package com.charles.spider.scheduler.job;

import org.junit.Test;

import java.nio.file.*;

/**
 * Created by lq on 17-3-31.
 */
public class JobCoreFactoryTest {
    @Test
    public void get() throws Exception {
    }

    @Test
    public void add() throws Exception {

//        TimerObject obj = new TimerObject("test","/5 * * * * ?",);
//        obj.setCron("/5 * * * * ?");
//        obj.setName("test");
//
//        JobCoreFactory.instance().submit(obj);

        //JobCoreFactory.instance().start();
        //System.in.read();

        //LinkedBuffer.allocate()

//        ObjectMapper mapper = new ObjectMapper();
//
//
//
//        Object[] o = new Object[]{new byte[]{1,2,3},1, true, "abc"};
//
//        System.out.println( mapper.writeValueAsString(o));
//
//
//        byte[] data = mapper.writeValueAsBytes(o);
//
//        JsonNode node = mapper.readTree(data);
//
//        Iterator<JsonNode> it = node.iterator();
//        if (it.hasNext()){
//            JsonNode x = it.next();
//
//            byte[] result = mapper.readValue(x.traverse(),byte[].class);
//
//            System.out.println(Arrays.toString(result));
//
//
//        }
//        Schema schema = RuntimeSchema.getSchema(o.getClass());
//
//        System.out.println(schema.typeClass());

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:www.baidu.com/*.html");
        Path p = Paths.get("www.baidu.com/index.html?");

        System.out.println(matcher.matches(p));
    }

}