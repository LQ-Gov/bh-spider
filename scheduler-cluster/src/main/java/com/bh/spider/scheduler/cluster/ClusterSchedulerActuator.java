package com.bh.spider.scheduler.cluster;

import com.bh.common.utils.CommandCode;
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
 * @version : ClusterSchedulerAc, 2019-05-24 19:20 liuqi19
 */
public class ClusterSchedulerActuator implements Actuator {
    private final static ObjectMapper mapper = Json.get();

    private Scheduler scheduler;


    public ClusterSchedulerActuator(Scheduler scheduler) {

        this.scheduler = scheduler;

    }


    @Override
    public byte[] snapshot() {
        return new byte[0];
    }

    @Override
    public void recover(byte[] data) {

    }

    @Override
    public void apply(byte[] entry) throws Exception {


        Iterator<JsonNode> it = mapper.readTree(entry).iterator();

        CommandCode key = CommandCode.values()[it.next().asInt()];

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
