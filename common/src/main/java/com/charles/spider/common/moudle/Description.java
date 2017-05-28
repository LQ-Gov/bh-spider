package com.charles.spider.common.moudle;

/**
 * Created by lq on 17-4-8.
 */
public class Description {
    private String name;
    private ModuleType type = ModuleType.HANDLE;
    private String info;
    private String hash;


    public Description(){}

    public Description(ModuleType type){
        this(null,type,null);
    }

    public Description(String name) {
        this(name,null);
    }

    public Description(String name,String info){
        this(name,ModuleType.HANDLE,info);
    }


    public Description(String name,ModuleType type,String info) {
        this.name = name;
        this.type = type;
        this.info = info;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ModuleType getType() {
        return type;
    }

    public void setType(ModuleType type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
