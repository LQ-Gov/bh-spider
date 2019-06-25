package com.bh.spider.scheduler.watch;

import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.CommandHandler;
import com.bh.spider.scheduler.event.Interceptor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.el.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author liuqi19
 * @version WatchInterceptor, 2019-06-24 16:58 liuqi19
 **/
public class WatchInterceptor implements Interceptor {
    private final static Logger logger = LoggerFactory.getLogger(WatchInterceptor.class);

    @Override
    public boolean before(String key, CommandHandler mapping, Context ctx, Method method, Object[] args) {
        logger.info(Markers.EVENT_LOOP, "execute command:{},params bytes size:{}", key, 0);

        Watch watch = method.getAnnotation(Watch.class);

        if (watch != null && StringUtils.isNotBlank(watch.text()) && StringUtils.isNotBlank(watch.value())) {

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


            String text = (String) factory.createValueExpression(elContext, watch.text(), String.class).getValue(elContext);

            Marker marker = MarkerFactory.getMarker(watch.value());

            logger.info(marker, text);
        }
        return true;
    }

    @Override
    public void after(Method method,Object returnValue) {



    }
}
