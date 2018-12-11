package com.bh.spider.transfer.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by lq on 17-6-21.
 */
public class Component implements Serializable {
    public enum State {
        NULL, TMP, VALID
    }

    public enum Type {
        UNKNOWN,
        CONFIG,
        COMMON,
        EXTRACTOR,
        SYSTEM,
        JAR,
        GROOVY



    }

    private String name;
    private String hash;
    private Type type;
    private String description;

    public Component(){}


    public Component(String name,Type type){
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
