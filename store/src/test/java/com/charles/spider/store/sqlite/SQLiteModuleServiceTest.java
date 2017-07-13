package com.charles.spider.store.sqlite;

import com.charles.spider.common.constant.ModuleTypes;
import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.common.entity.Module;
import org.junit.Test;

/**
 * Created by lq on 17-6-26.
 */
public class SQLiteModuleServiceTest {


    @Test
    public void save() throws Exception {
        Module entity = new Module();
        entity.setDetail("test module store");
        entity.setType(ModuleTypes.JAR);
        entity.setHash("ettssgd2330dsf0");
        entity.setPath("/data/mod");
        entity.setName("test");

        //SQLiteBuilder.build().module().save(entity);
        System.out.println(entity.getId());


    }

    @Test
    public void select() throws Exception {

        Query query = new Query();
        query.addCondition(Condition.where("id").is(2));
//        List<Module> list = SQLiteBuilder.build().module().select(query);
//        list.forEach(x->System.out.println(x.getId()));
    }

    @Test
    public void delete() throws Exception {
    }

    @Test
    public void update() throws Exception {
    }

}