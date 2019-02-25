package com.bh.spider.common.fetch;

import java.lang.annotation.*;

/**
 * 标志处理哪些responseCode
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Code {

    String[] value() default {};
}
