package com.bh.spider.scheduler.cluster.consistent.operation;

import com.bh.common.utils.Json;
import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.scheduler.cluster.context.RaftContext;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.Interceptor;
import org.apache.commons.lang3.StringUtils;

import javax.el.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OperationInterceptor implements Interceptor {
    private Raft raft;

    public OperationInterceptor(Raft raft) {
        this.raft = raft;
    }


    @Override
    public boolean before(String key, CommandHandler mapping, Context ctx, Method method, Object[] args) {
        Operation operation = method.getAnnotation(Operation.class);
        if (operation != null) {

            //进行Raft同步
            if (!(ctx instanceof RaftContext) && operation.sync()) {
                List<Object> items = new LinkedList<>();

                String methodName = method.getName();
                items.add(StringUtils.isBlank(mapping.value()) ?
                        methodName.substring(0, methodName.length() - "_HANDLER".length()) : mapping.value());


                Arrays.stream(args).filter(x -> !(x instanceof Context)).forEach(items::add);


                try {
                    byte[] data = Json.get().writeValueAsBytes(items);
                    CompletableFuture future = raft.write(data);

                    future.get();

                    return false;

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }


            ExpressionFactory factory = ExpressionFactory.newInstance();
            ELContext elContext = new StandardELContext(factory);


            Parameter[] parameters = method.getParameters();

            VariableMapper mapper = elContext.getVariableMapper();

            for (int i = 0; i < parameters.length; i++) {
                Object arg = args[i];
                if (arg != null) {
                    ValueExpression expression = factory.createValueExpression(arg, arg.getClass());
                    mapper.setVariable(parameters[i].getName(), expression);
                }
            }


            String data = (String) factory.createValueExpression(elContext, operation.data(), String.class).getValue(elContext);

            Entry entry = new Entry(operation.action(), data.getBytes());

            return OperationRecorderFactory.get(operation.group()).write(entry);
        }

        return true;
    }

    @Override
    public void after(Method method, Object returnValue) {

    }
}
