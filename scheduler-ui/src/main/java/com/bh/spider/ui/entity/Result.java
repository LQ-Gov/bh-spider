package com.bh.spider.ui.entity;

public class Result {
    private int code;
    private String msg;
    private Object data;

    public Result(int code){
        this(code,null,null);
    }

    public Result(int code,Object data){
        this(code,data,null);
    }

    public Result(int code,Object data,String msg){
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
