package com.bh.spider.scheduler.domain;

import com.bh.spider.fetch.Code;
import com.bh.spider.fetch.Extractor;
import com.bh.spider.fetch.FetchContext;
import com.bh.spider.scheduler.Scheduler;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

public class ExtractFacade {
    private final static Map<String,ExtractFacade> cache = new ConcurrentHashMap<>();

    private Context ctx;
    private Extractor extractor;
    private ExtractFacade( Context ctx,Extractor extractor) {
        this.ctx = ctx;
        this.extractor = extractor;
    }


    public static ExtractFacade facadeAsync(Scheduler scheduler, Context ctx, String name)throws Exception {
        Future<Callable<Class<Extractor>>> future = scheduler.process(new Command(ctx, CommandCode.LOAD_COMPONENT_ASYNC,
                new Object[]{name, Component.Type.GROOVY}));

        Callable<Class<Extractor>> callable = future.get();

        Class<Extractor> cls = callable.call();
        if(cls==null) throw new Exception("无法加载class");

        return new ExtractFacade(ctx,cls.newInstance());
    }

    public static ExtractFacade facade(Scheduler scheduler, Context ctx, String name) throws Exception {
        Future<Class<Extractor>> future = scheduler.process(new Command(ctx, CommandCode.LOAD_COMPONENT,
                new Object[]{name, Component.Type.GROOVY}));

        Class<Extractor> cls = future.get();
        if(cls!=null){
            return new ExtractFacade(ctx,cls.newInstance());
        }

        return null;
    }


    public void exec(FetchContext fetchContext) throws Exception {
        String code = String.valueOf(fetchContext.response().code());
        Method[] methods = MethodUtils.getMethodsWithAnnotation(this.extractor.getClass(), Code.class);
        for (Method method : methods) {
            Code annotation = method.getAnnotation(Code.class);
            if (ArrayUtils.contains(annotation.value(), code)) {
                invoke(this.ctx, fetchContext, this.extractor, method);
                return;
            }
        }
        invoke(this.ctx, fetchContext, this.extractor, this.extractor.getClass().getMethod("run", FetchContext.class));
    }

    private void invoke(Context ctx, FetchContext fetchContext, Extractor obj, Method method) throws Exception {
        if (method.getParameterTypes().length > 0) {
            Class<?> firstParameterClass = method.getParameterTypes()[0];
            if (firstParameterClass.isAssignableFrom(fetchContext.getClass())) {
                method.invoke(obj, fetchContext);
            }
        } else method.invoke(obj);
    }
}
