package com.bh.spider.scheduler.watch;

import java.lang.annotation.*;

/**
 * @author liuqi19
 * @version Watch, 2019-06-24 16:45 liuqi19
 **/
@Inherited
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Watch {
    String value() default "";

    String text() default "";
}
