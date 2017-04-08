package com.charles.spider.common.moudle;

/**
 * Created by lq on 17-4-8.
 */
public class Description {
    private String name;
    private String path;
    private ModuleType type = ModuleType.HANDLE;
    private String info;

    public Description(String name) {
        this(name, null, ModuleType.HANDLE, null);
    }

    public Description(String name,String info){
        this(name,null,ModuleType.HANDLE,info);
    }

    public Description(String name,ModuleType type,String info){
        this(name,null,type,info);
    }

    public Description(String name,String path,ModuleType type,String info) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.info = info;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
}
