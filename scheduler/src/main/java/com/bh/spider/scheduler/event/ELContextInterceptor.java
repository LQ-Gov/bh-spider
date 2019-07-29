package com.bh.spider.scheduler.event;

import com.bh.spider.scheduler.context.Context;

import javax.el.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author liuqi19
 * @version ELContextInterceptor, 2019-07-02 11:29 liuqi19
 **/
public abstract class ELContextInterceptor implements Interceptor {
    private final static ExpressionFactory factory = ExpressionFactory.newInstance();

    private ThreadLocal<ELContext> elContextThreadLocal = new ThreadLocal<>();

    @Override
    public boolean before(String key, Context ctx, Method method, Object[] args) {

        ELContext elContext = elContextThreadLocal.get();

        if(elContext==null) {

            elContext = new StandardELContext(factory);

            Parameter[] parameters = method.getParameters();

            VariableMapper mapper = elContext.getVariableMapper();


            for (int i = 0; i < parameters.length; i++) {
                Object arg = args[i];
                if (arg != null) {
                    ValueExpression expression = factory.createValueExpression(arg, arg.getClass());
                    mapper.setVariable(parameters[i].getName(), expression);
                }
            }

            elContextThreadLocal.set(elContext);
        }


        return before(elContext,key,ctx,method,args);
    }



    public abstract boolean before(ELContext elContext,String key,Context ctx,Method method,Object[] args);



    protected ExpressionFactory expressionFactory(){
        return factory;
    }




    @Override
    public void after(Method method, Object returnValue) {
        ELContext elContext = elContextThreadLocal.get();
        elContext.getVariableMapper().setVariable("returnValue",
                returnValue == null ? null : factory.createValueExpression(returnValue, returnValue.getClass()));

        after(elContext, method, returnValue);


        elContextThreadLocal.remove();
    }


    public abstract void after(ELContext elContext,Method method,Object returnValue);
}
