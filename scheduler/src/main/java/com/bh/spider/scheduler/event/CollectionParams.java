package com.bh.spider.scheduler.event;

import java.lang.annotation.*;
import java.util.Collection;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CollectionParams {

    Class<? extends Collection> collectionType();

    Class<?>[] argumentTypes();
}
