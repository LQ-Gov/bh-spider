package com.charles.spider.scheduler.event;

import com.charles.common.spider.command.Commands;
import com.charles.spider.common.protocol.Token;
import com.charles.spider.scheduler.Command;
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
        queue.offer(new Command(type,params));

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

                Object[] args = check_and_build_args(parameters, cmd.params());

                executor.invoke(args);
            } catch (InterruptedException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected Object[] check_and_build_args(Class<?>[] parameters,Object[] inputs) throws Exception {
        if(parameters.length!=inputs.length) throw new Exception("error input length");
        if(parameters.length==0) return null;

        Object[] args = new Object[parameters.length];

        for(int i=0;i<parameters.length;i++) {
            if (parameters[i].getClass().isAssignableFrom(inputs[i].getClass()))
                args[i] = inputs[i];
            else if (inputs[i].getClass() == Token.class)
                args[i] = ((Token) inputs[i]).toClass(parameters[i].getClass());
            else throw new Exception("error input type");
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
