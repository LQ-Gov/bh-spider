package com.bh.spider.scheduler.domain;

import com.bh.spider.fetch.*;
import com.bh.spider.rule.ExtractQueue;
import com.bh.spider.scheduler.BasicScheduler;
import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.Command;
import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ExtractQueueFacade {

    private BasicScheduler scheduler;
    private String name;
    private ExtractorLoader[] loaders;


    public ExtractQueueFacade(BasicScheduler scheduler, ExtractQueue queue) {


        this.scheduler = scheduler;
        this.name = queue.getName();
        String[] chain = queue.getChain();
        if (chain != null && chain.length > 0) {
            loaders = new ExtractorLoader[chain.length];
            for (int i = 0; i < chain.length; i++) {
                loaders[i] = new ExtractorLoader(chain[i], null);
            }
        }

    }


    public void extract(Context ctx, FetchContext fetchContext) throws Exception {
        if (loaders == null || loaders.length == 0) return;


        int code = fetchContext.response().code();
        for (ExtractorLoader loader : loaders) {

            Method method = loader.method(ctx, String.valueOf(code));

            Extractor obj = loader.cls.newInstance();

            try {
                invoke(ctx, fetchContext, obj, method);
            } catch (ExtractorChainException e) {
                if (e.result() == Behaviour.TERMINATION) break;
            } catch (Exception e) {
                scheduler.process(new Command(ctx, CommandCode.REPORT_EXCEPTION, new Object[]{fetchContext.request().id(), e.getMessage()}));
                throw e;
            }
        }
    }

    private void invoke(Context ctx, FetchContext fetchContext, Extractor obj, Method method) throws Exception {
        if (method.getParameterTypes().length > 0) {
            Class<?> firstParameterClass = method.getParameterTypes()[0];
            if (firstParameterClass.isAssignableFrom(fetchContext.getClass())) {
                method.invoke(obj, fetchContext);
            }
        } else method.invoke(obj);
    }


    private class ExtractorLoader {
        private String name;
        private Class<? extends Extractor> cls;
        private Map<String, Method> codeHandlers = new HashMap<>();

        public ExtractorLoader(String name, Class<? extends Extractor> cls) {
            this.name = name;
            this.cls = cls;
        }


        public String name() {
            return name;
        }


        public Method method(Context ctx, String code) throws Exception {
            loadClass(ctx);

            if (cls == null) throw new Exception("执行类不存在");

            Method returnValue = codeHandlers.get(code);


            if (returnValue == null) {
                List<Method> methods = MethodUtils.getMethodsListWithAnnotation(cls, Code.class, true, true);
                for (Method m : methods)
                    cacheHandlers(m);

                returnValue = codeHandlers.get(code);
            }

            return returnValue == null ? cls.getMethod("run", FetchContext.class) : returnValue;


        }

        private void cacheHandlers(Method method) {
            Code code = MethodUtils.getAnnotation(method, Code.class, true, true);
            String[] values = code.value();
            for (String it : values) {
                codeHandlers.putIfAbsent(it, method);
            }
        }

        private synchronized void loadClass(Context ctx) throws ExecutionException, InterruptedException {

            Future<Class<Extractor>> future = scheduler.process(new Command(ctx, CommandCode.LOAD_COMPONENT,
                    new Object[]{name, Component.Type.GROOVY}));
            cls = future.get();

        }
    }
}
