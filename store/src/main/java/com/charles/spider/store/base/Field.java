package com.charles.spider.store.base;
/**
 * Created by lq on 17-3-24.
 */

public class Field {
    private String name;
    private Object value;

    public Field(String name){
        this(name,null);
    }

    public Field(String name,Object value){
        this.name = name;
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
