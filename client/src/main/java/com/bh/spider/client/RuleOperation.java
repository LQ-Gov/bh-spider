package com.bh.spider.client;

import com.bh.common.utils.CommandCode;
import com.bh.common.utils.Json;
import com.bh.spider.common.rule.Rule;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by lq on 7/9/17.
 */
public class RuleOperation {

    private Communicator communicator = null;

    RuleOperation(Communicator communicator) {
        this.communicator = communicator;
    }


    public void submit(String rule) throws IOException {
        Rule o = Json.get().readValue(rule, Rule.class);

        submit(o);
    }


    public void submit(Rule rule) {
        long id = communicator.write(CommandCode.ID_GENERATOR, Long.class);
        rule.setId(id);
        communicator.write(CommandCode.SUBMIT_RULE, null, rule);
    }

    public List<Rule> select() {
        return select(null);

    }

    public List<Rule> select(String host) {
        ParameterizedType type = ParameterizedTypeImpl.make(List.class, new Type[]{Rule.class}, null);
        List<Rule> rules = communicator.write(CommandCode.GET_RULE_LIST, type, host);

        return rules == null ? Collections.emptyList() : rules;
    }


    public Rule select(long id) {
        return communicator.write(CommandCode.GET_RULE, Rule.class, id);
    }

    public void edit(Rule rule) {
        communicator.write(CommandCode.EDIT_RULE, null, rule);
    }

    public void delete(long id) {
        communicator.write(CommandCode.DELETE_RULE, null, id);
    }


    public void run(String host, String id) {
        communicator.write(CommandCode.SCHEDULER_RULE_EXECUTOR, null, host, id, true);
    }

    public void pause(String host, String id) {
        communicator.write(CommandCode.SCHEDULER_RULE_EXECUTOR, null, host, id, false);
    }


}
