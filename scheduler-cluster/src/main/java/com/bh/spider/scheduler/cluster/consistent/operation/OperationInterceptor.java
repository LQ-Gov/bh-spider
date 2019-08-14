package com.bh.spider.scheduler.cluster.consistent.operation;

import com.bh.common.utils.Json;
import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.scheduler.IdGenerator;
import com.bh.spider.scheduler.cluster.actuator.CombineActuator;
import com.bh.spider.scheduler.cluster.context.ConsistentContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.ELContextInterceptor;
import org.apache.commons.lang3.StringUtils;

import javax.el.ELContext;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OperationInterceptor extends ELContextInterceptor {
    private Raft raft;

    private final static Map<Long, Context> WAIT_CONSISTENT_CONTEXT = new ConcurrentHashMap<>();

    public OperationInterceptor(Raft raft) {
        this.raft = raft;
    }

    @Override
    public boolean before(ELContext elContext, String key, Context ctx, Method method, Object[] args) {
        Operation operation = method.getAnnotation(Operation.class);

        if (operation == null) return true;


        if (operation.sync()) {
            if (ctx instanceof ConsistentContext) {
                ConsistentContext consistentContext = (ConsistentContext) ctx;
                Context beforeContext = WAIT_CONSISTENT_CONTEXT.get(consistentContext.consistentId());

                if (beforeContext != null) {
                    ((ConsistentContext) ctx).transform(beforeContext);
                }
            }else {
                //进行Raft同步

                long consistentId = IdGenerator.instance.nextId();

                List<Object> items = new LinkedList<>(Arrays.asList(key, consistentId));

                Arrays.stream(args).filter(x -> !(x instanceof Context)).forEach(items::add);


                try {
                    byte[] data = Json.get().writeValueAsBytes(items);
                    CombineActuator.CombineEntry combineEntry = new CombineActuator.CombineEntry("COMMAND_ACTUATOR", data);
                    data = Json.get().writeValueAsBytes(combineEntry);
                    raft.write(data);
                    WAIT_CONSISTENT_CONTEXT.put(consistentId, ctx);
                    return false;

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }

        }

        if (StringUtils.isNotBlank(operation.data())) {


            String data = (String) expressionFactory().createValueExpression(elContext, operation.data(), String.class).getValue(elContext);
            Entry entry = new Entry(operation.action(), data.getBytes());
            return OperationRecorderFactory.get(operation.group()).write(entry);
        }
        return true;

    }


    @Override
    public void after(ELContext elContext, Method method, Object returnValue) {

    }
}
