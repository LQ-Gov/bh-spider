package com.bh.spider.scheduler.watch;

import com.bh.spider.scheduler.context.Context;
import com.bh.spider.scheduler.event.ELContextInterceptor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import javax.el.ELContext;
import java.lang.reflect.Method;

/**
 * @author liuqi19
 * @version WatchInterceptor, 2019-06-24 16:58 liuqi19
 **/
public class WatchInterceptor extends ELContextInterceptor {
    private final static Logger logger = LoggerFactory.getLogger(WatchInterceptor.class);


    @Override
    public boolean before(ELContext elContext, String key, Context ctx, Method method, Object[] args) {
        logger.info(Markers.EVENT_LOOP, "execute command:{},params bytes size:{}", key, 0);


        return true;
    }

    @Override
    public void after(ELContext elContext, Method method, Object returnValue) {

        Watch watch = method.getAnnotation(Watch.class);

        if (watch != null && StringUtils.isNotBlank(watch.log()) && StringUtils.isNotBlank(watch.value())) {

            String text = (String) expressionFactory().createValueExpression(elContext, watch.log(), String.class).getValue(elContext);


            Object[] params = new Object[watch.params().length];
            if (ArrayUtils.isNotEmpty(watch.params())) {
                for (int i = 0; i < params.length; i++) {
                    Object value = expressionFactory().createValueExpression(elContext, watch.params()[i], Object.class).getValue(elContext);
                    params[i] = value;
                }
            }

            logger.info(MarkerFactory.getMarker(watch.value()), text, params);
        }

    }
}
