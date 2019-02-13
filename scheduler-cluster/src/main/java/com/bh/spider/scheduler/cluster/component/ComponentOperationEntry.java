package com.bh.spider.scheduler.cluster.component;

import com.bh.spider.scheduler.cluster.consistent.operation.Entry;
import com.bh.spider.transfer.entity.Component;

public class ComponentOperationEntry {
    public final static String ADD = "ADD";
    public final static String DELETE = "DELETE";

    private long index;

    private String operation;

    private String name;

    private Component.Type type;

    public ComponentOperationEntry(Entry entry) {
        this.index = entry.index();

        String[] block = new String(entry.data()).split(" ");

        operation = block[0];

        name = block[1];


        if(block.length>2) type = Component.Type.valueOf(block[2]);
    }


    public long index() {
        return index;
    }

    public String operation() {
        return operation;
    }

    public String name() {
        return name;
    }


    public Component.Type type(){return type;}
}
