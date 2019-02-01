package com.bh.spider.fetch;

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
