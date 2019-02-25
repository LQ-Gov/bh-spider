package com.bh.spider.client;

import com.bh.spider.client.sender.Sender;
import com.bh.common.utils.CommandCode;
import com.bh.common.utils.Json;
import com.bh.spider.common.rule.Rule;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by lq on 7/9/17.
 */
public class RuleOperation {

    private Sender sender = null;

    RuleOperation(Sender sender) {
        this.sender = sender;
    }


    public void submit(String rule) throws IOException {
        Rule o = Json.get().readValue(rule, Rule.class);

        submit(o);
    }


    public void submit(Rule rule) {
        sender.write(CommandCode.SUBMIT_RULE, null, rule);
    }

    public List<Rule> select() {
        return select(null);

    }

    public List<Rule> select(String host) {
        ParameterizedType type = ParameterizedTypeImpl.make(List.class, new Type[]{Rule.class}, null);
        return sender.write(CommandCode.GET_RULE_LIST, type, host);
    }

    /**
     * 获取HOST集合
     *
     * @return
     */
    public List<String> hosts() {
        ParameterizedType type = ParameterizedTypeImpl.make(List.class, new Type[]{String.class}, null);
        return sender.write(CommandCode.GET_HOST_LIST, type);
    }

    public void delete(String host, String uuid) {
        sender.write(CommandCode.DELETE_RULE, null, host, uuid);
    }


    public void run(String host, String id) {
        sender.write(CommandCode.SCHEDULER_RULE_EXECUTOR, null, host, id, true);
    }

    public void pause(String host, String id) {
        sender.write(CommandCode.SCHEDULER_RULE_EXECUTOR, null, host, id, false);
    }


//    public void edit(String host,String id,Rule rule){
//         client.write(Commands.EDIT_RULE,null, host,id,rule);
//    }


}
