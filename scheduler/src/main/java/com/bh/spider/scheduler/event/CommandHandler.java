package com.bh.spider.scheduler.event;

import java.lang.annotation.*;

/**
 * Created by lq on 17-4-12.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CommandHandler {
    String value() default "";

    boolean disabled() default false;

    boolean autoComplete() default true;

    String cron() default "";

    /**
     * 命令最小执行间隔,单位毫秒
     * @return
     */
    long minInterval() default 0;


}
