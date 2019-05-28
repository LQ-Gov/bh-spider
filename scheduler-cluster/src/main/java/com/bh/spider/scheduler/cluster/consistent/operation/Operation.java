package com.bh.spider.scheduler.cluster.consistent.operation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Operation {
    byte WRITE=1;
    byte READ=0;

    String group() default "operation";


    byte action();


    String data();


    boolean sync() default true;



}
