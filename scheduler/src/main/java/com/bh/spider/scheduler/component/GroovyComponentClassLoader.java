package com.bh.spider.scheduler.component;

import groovy.lang.GroovyClassLoader;

public class GroovyComponentClassLoader extends GroovyClassLoader {


    public GroovyComponentClassLoader(ClassLoader parent){
        super(parent);
    }
}
