package com.charles.spider.scheduler.event;

import com.charles.common.spider.command.Commands;
import com.charles.common.utils.ArrayUtils;
import com.charles.spider.common.protocol.Token;
import com.charles.spider.scheduler.Command;
import com.charles.spider.scheduler.context.Context;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private BlockingQueue<Command> queue = new LinkedBlockingQueue<>();
    private Map<String, MethodExecutor> resolvers = new HashMap<>();

    public EventLoop(IEvent parent) {
        this.parent = parent;
        init_process_methods(this.parent);

    }

    public Future execute(Commands type, Object... params) {
        queue.offer(new Command(type, params));

        return null;

    }

    @Override
    public void run() {
        while (!this.parent.isClosed()) {
            try {
                Command cmd = queue.take();
                if (this.parent.isClosed()) break;
                if (cmd == null) continue;

                logger.info("execute command {}", cmd.key());

                MethodExecutor executor = resolvers.get(cmd.key().toString());


                Class<?>[] parameters = executor.getParameters();

                Object[] args = buildArgs(parameters, cmd.params());

                executor.invoke(args);
            } catch (InterruptedException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("after process");
            }
        }
    }


    protected Object[] buildArgs(Class<?>[] parameters, Object[] inputs) {
        if (parameters.length == 0) return null;

        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {

            if (Context.class.isAssignableFrom(parameters[i]))
                args[i] = null;//赋值给其context
            else if (parameters[i].isAssignableFrom(inputs[i].getClass()))
                args[i] = inputs[i];

            else if(parameters[i].isPrimitive()&&parameters[i]== ClassUtils.wrapperToPrimitive(inputs[i].getClass()))
                args[i]= inputs[i];

            else if (parameters[i].isArray() && inputs[i].getClass().isArray()) {
                Class<?> componentType = parameters[i].getComponentType();
                if(componentType.isPrimitive()
                        &&ClassUtils.wrapperToPrimitive(inputs[i].getClass().getComponentType())==componentType)
                    args[i] = ArrayUtils.toPrimitive(inputs[i]);

                else {
                    //如果不是基本类型,就依次进行转化
                }
            } else if (Token.class.isAssignableFrom(inputs[i].getClass())) {
                try {
                    args[i] = ((Token) inputs[i]).toClass(parameters[i]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("the runtime token can't cast to " + parameters[i].toString() + ",index:" + i,e);
                }
            } else
                throw new IllegalArgumentException("can't cast to " + parameters[i].toString() + ",index:" + i);
        }
        return args;

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
                resolvers.put(key, new MethodExecutor(o, method));
            }
        }
    }
}
