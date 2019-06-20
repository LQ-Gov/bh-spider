package com.bh.spider.common.fetch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liuqi19
 * @version Contract, 2019-06-04 16:38 liuqi19
 **/

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Contract  {
    String value() default "";

    Class<?> implementFor() default Object.class;
}
