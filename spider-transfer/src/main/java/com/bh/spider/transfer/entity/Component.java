package com.bh.spider.transfer.entity;

import com.sun.org.apache.bcel.internal.generic.RET;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by lq on 17-6-21.
 */
public class Component implements Serializable,Cloneable {
    public enum State {
        NULL, TMP, VALID
    }

    public enum Type {
        UNKNOWN("unknown"),
        CONFIG("config"),
        COMMON("common"),
        EXTRACTOR("extractor"),
        SYSTEM("system"),
        JAR("jar"),
        GROOVY("groovy");


        private String text;

        Type(String text){
            this.text = text;
        }


        public String text(){return text;}


        public static Type textOf(String text){
            switch (text.toLowerCase()){
                case "jar": return JAR;
                case "groovy": return GROOVY;
            }
            return null;
        }



    }

    private String name;
    private String extension;
    private String hash;
    private Type type;
    private String description;
    private boolean valid;
    private Date createTime;

    private byte[] data;

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

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public Component clone() throws CloneNotSupportedException {
        Component o = (Component) super.clone();
        return o;
    }
}
