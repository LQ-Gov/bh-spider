package com.bh.spider.scheduler.cluster.actuator;

import com.bh.common.utils.CommandCode;
import com.bh.common.utils.Json;
import com.bh.spider.consistent.raft.Actuator;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.cluster.context.ConsistentContext;
import com.bh.spider.scheduler.context.LocalContext;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.token.JacksonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.Future;

/**
 * @author liuqi19
 * @version CommandActuator, 2019-08-08 17:37 liuqi19
 **/
public class CommandActuator implements Actuator {
    private final static ObjectMapper mapper = Json.get();

    private Scheduler scheduler;

    public CommandActuator(Scheduler scheduler) {
        this.scheduler = scheduler;

    }

    @Override
    public String name() {
        return "COMMAND_ACTUATOR";
    }

    @Override
    public byte[] snapshot() {
        LocalContext ctx = new LocalContext(scheduler);

        try {

            byte[] snap1 = scheduler.<byte[]>process(new Command(ctx, CommandCode.COMPONENT_SNAPSHOT.name())).get();

            byte[] snap2 = scheduler.<byte[]>process(new Command(ctx, CommandCode.RULE_SNAPSHOT.name())).get();

            Map<String, byte[]> map = new HashMap<>();
            map.put("component", snap1);
            map.put("rule", snap2);

            return Json.get().writeValueAsBytes(map);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public void recover(byte[] data) throws Exception {
        LocalContext ctx = new LocalContext(scheduler);
        if (data == null) {
            scheduler.process(new Command(ctx, CommandCode.APPLY_COMPONENT_SNAPSHOT.name(), (Object) null));
            scheduler.process(new Command(ctx, CommandCode.APPLY_RULE_SNAPSHOT.name(), (Object) null));

        } else {
            Map<String, byte[]> map = Json.get().readValue(data, Json.mapType(String.class, byte[].class));
            scheduler.process(new Command(ctx, CommandCode.APPLY_COMPONENT_SNAPSHOT.name(), map.get("component")));
            scheduler.process(new Command(ctx, CommandCode.APPLY_RULE_SNAPSHOT.name(), map.get("rule")));
        }


    }

    @Override
    public void apply(byte[] entry) throws Exception {
        Iterator<JsonNode> it = mapper.readTree(entry).iterator();


        String key = it.next().asText();

        long consistentId = it.next().asLong();

        ConsistentContext ctx = new ConsistentContext(consistentId);

        List<Object> params = new LinkedList<>();
        it.forEachRemaining(node -> params.add(new JacksonToken(mapper, node.traverse())));


        Command cmd = new Command(ctx, key, params.toArray());


        Future<Object> future = this.scheduler.process(cmd);
    }

    @Override
    public Object read(byte[] data, boolean wait) {
        return null;
    }
}
