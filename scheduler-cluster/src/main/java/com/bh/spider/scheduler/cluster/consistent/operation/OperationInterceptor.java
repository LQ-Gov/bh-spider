package com.bh.spider.scheduler.cluster.consistent.operation;

import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.ELContextInterceptor;

import javax.el.ELContext;
import java.lang.reflect.Method;

public class OperationInterceptor extends ELContextInterceptor {
    private Raft raft;

    public OperationInterceptor(Raft raft) {
        this.raft = raft;
    }

    @Override
    public boolean before(ELContext elContext, String key, CommandHandler mapping, Method method, Object[] args) {
        Operation operation = method.getAnnotation(Operation.class);

//        List<Object> items = Arrays.stream(args).filter(x -> !(x instanceof Context)).collect(Collectors.toList());


        //进行Raft同步
//            if (!(ctx instanceof RaftContext) && operation.sync()) {
//                List<Object> items = new LinkedList<>();
//
//                String methodName = method.getName();
//                items.add(StringUtils.isBlank(mapping.value()) ?
//                        methodName.substring(0, methodName.length() - "_HANDLER".length()) : mapping.value());
//
//
//                Arrays.stream(args).filter(x -> !(x instanceof Context)).forEach(items::add);
//
//
//                try {
//                    byte[] data = Json.get().writeValueAsBytes(items);
//                    CompletableFuture future = raft.write(data);
//
//                    future.get();
//
//                    return false;
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false;
//                }
//            }

        if(operation!=null){
            String data = (String) expressionFactory().createValueExpression(elContext,operation.data(),String.class).getValue(elContext);
            Entry entry = new Entry(operation.action(),data.getBytes());
            return OperationRecorderFactory.get(operation.group()).write(entry);
        }

        return true;
    }



    @Override
    public void after(ELContext elContext, Method method, Object returnValue) {

    }
}
