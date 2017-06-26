package com.charles.spider.common.moudle;

/**
 * Created by lq on 17-4-8.
 */
public class Description {
    private String name;
    private ModuleType type = ModuleType.UNKNOWN;
    private String detail;


    public Description(){}

    public Description(ModuleType type){
        this(null,type,null);
    }

    public Description(String name) {
        this(name,null);
    }

    public Description(String name,String detail){
        this(name,ModuleType.JAR,detail);
    }


    public Description(String name,ModuleType type,String detail) {
        this.name = name;
        this.type = type;
        this.detail = detail;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
