package com.charles.spider.client;

import com.charles.common.JsonFactory;
import com.charles.spider.common.command.Commands;
import com.charles.spider.common.entity.Rule;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by lq on 7/9/17.
 */
public class RuleOperation {

    private Client client = null;

    RuleOperation(Client client) {
        this.client = client;
    }


    public void submit(String rule) throws IOException {
        Rule o = JsonFactory.get().readValue(rule, Rule.class);

        submit(o);
    }


    public void submit(Rule rule) {
        client.write(Commands.SUBMIT_RULE, null, rule);
    }

    public List<Rule> select(){
        return select(null,0,-1);

    }

    public List<Rule> select(String host,int skip,int size) {
        ParameterizedType type = ParameterizedTypeImpl.make(List.class, new Type[]{Rule.class}, null);
        return client.write(Commands.GET_RULE_LIST,type, host,skip,size);
    }

    /**
     * 获取HOST集合
     * @return
     */
    public List<String> hosts() {
        ParameterizedType type = ParameterizedTypeImpl.make(List.class, new Type[]{String.class}, null);
        return client.write(Commands.GET_HOST_LIST, type);
    }

    public void delete(String host,String uuid){
        client.write(Commands.DELETE_RULE,null,host,uuid);
    }


    public void run(String host,String id) {
        client.write(Commands.SCHEDULER_RULE_EXECUTOR, null, host, id, true);
    }

    public void pause(String host,String id){
        client.write(Commands.SCHEDULER_RULE_EXECUTOR,null,host,id,false);
    }

    public void destroy(String host,String id){
        client.write(Commands.DELETE_RULE,null,host,id);
    }

    public void edit(String host,String id,Rule rule){
         client.write(Commands.EDIT_RULE,null, host,id,rule);
    }



}
