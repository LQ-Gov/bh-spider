package com.charles.spider.scheduler.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.charles.common.Pair;
import com.charles.common.spider.command.Commands;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by lq on 17-3-16.
 */
public class EventLoop extends Thread {

    private Logger logger = LoggerFactory.getLogger(EventLoop.class);

    private IEvent parent = null;
    private BlockingQueue<Pair<Commands, Object[]>> queue = new LinkedBlockingQueue<>();
    private Map<String, MethodResolver> resolvers = new HashMap<>();

    public EventLoop(IEvent parent) {
        this.parent = parent;
        init_process_methods(this.parent);

    }

    public Future execute(Commands type, Object... params) {
        queue.offer(new Pair<>(type, params));

        return null;

    }

    @Override
    public void run() {
        while (!this.parent.isClosed()) {
            try {
                Pair<Commands, Object[]> cmd = queue.take();
                logger.debug("get one cmd");
                if (cmd == null) continue;

                logger.info("execute command {}", cmd.getFirst());

                MethodResolver resolver = resolvers.get(cmd.getFirst().toString());

                if (resolver == null) {
                    logger.error("no handler about {}", cmd.getFirst());
                    continue;
                }

                Class<?>[] parameters = resolver.getParameters();

                if (parameters.length < cmd.getSecond().length) {
                    logger.error("{}:input params sum error", cmd.getFirst());
                    continue;
                }
                Object[] args = new Object[parameters.length];
                if (parameters.length > 0) {

                    for (int i = 0; i < parameters.length; i++) {
                        Class<?> p = parameters[i];
                        Object arg = cmd.getSecond()[i];
                        if (p.isAssignableFrom(arg.getClass()))
                            args[i] = arg;
                        else if(arg.getClass().isArray()&&arg.getClass().getComponentType().equals(byte.class))
                            continue;
                        else{
                            logger.error("parameter type error:{}",cmd.getFirst());
                            continue;
                        }
                    }
                }
                try {
                    resolver.invoke(args);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    logger.error("{}:invoke function error", cmd.getFirst());
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    protected void init_process_methods(Object o) {
        Method[] methods = o.getClass().getDeclaredMethods();

        for (Method method : methods) {
            EventMapping mapping = method.getDeclaredAnnotation(EventMapping.class);
            if (mapping != null) {
                String key = mapping.value();
                if (StringUtils.isBlank(key)) {
                    key = method.getName();
                    if (key.endsWith("_HANDLER"))
                        key = key.substring(0, key.length() - "_HANDLER".length());
                }
                resolvers.put(key, new MethodResolver(o, method));
            }
        }
    }
}
