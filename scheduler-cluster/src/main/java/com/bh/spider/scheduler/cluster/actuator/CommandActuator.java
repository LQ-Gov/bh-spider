package com.bh.spider.scheduler.cluster.actuator;

import com.bh.common.utils.Json;
import com.bh.spider.consistent.raft.Actuator;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.cluster.context.ConsistentContext;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.scheduler.event.token.JacksonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
        return new byte[0];
    }

    @Override
    public void recover(byte[] data) throws Exception {

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
