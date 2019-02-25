package com.bh.spider.common.fetch.impl;


import com.bh.spider.common.fetch.Request;

import java.util.Date;

public class FetchState {

    private Request.State state;

    private Date updateTime;

    private String message;

    public FetchState() {
    }

    public FetchState(Request.State state, Date updateTime, String message) {
        this.state = state;
        this.message = message;
        this.updateTime = updateTime;
    }


    public Request.State getState() {
        return state;
    }

    public void setState(Request.State state) {
        this.state = state;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static FetchState queue() {
        return new FetchState(Request.State.QUEUE, null, null);
    }

    public static FetchState going() {
        return new FetchState(Request.State.GOING, new Date(), null);
    }

    public static FetchState finished() {
        return new FetchState(Request.State.FINISHED, new Date(), null);
    }

    public static FetchState failed(){
        return new FetchState(Request.State.FAILED,new Date(),null);
    }

    public static FetchState exception(String message){
        return new FetchState(Request.State.EXCEPTION,new Date(),message);
    }
}
