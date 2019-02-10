package com.bh.spider.scheduler.cluster.consistent.operation;

import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.EventMapping;
import com.bh.spider.scheduler.event.Interceptor;

import javax.el.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class OperationInterceptor implements Interceptor {
    @Override
    public boolean before(EventMapping mapping, Context ctx, Method method, Object[] args) {
        Operation operation = method.getAnnotation(Operation.class);
        if(operation!=null) {

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
    public void after() {

    }
}
