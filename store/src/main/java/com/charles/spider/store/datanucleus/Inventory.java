package com.charles.spider.store.datanucleus;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

//@PersistenceCapable
public class Inventory {
    @PrimaryKey
    protected String name=null;


    public Inventory(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return "Inventory : " + name;
    }
}
