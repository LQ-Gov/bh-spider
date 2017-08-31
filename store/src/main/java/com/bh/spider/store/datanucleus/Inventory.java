package com.bh.spider.store.datanucleus;


//@PersistenceCapable
public class Inventory {
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
