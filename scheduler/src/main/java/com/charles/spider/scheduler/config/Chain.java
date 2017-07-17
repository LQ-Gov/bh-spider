package com.charles.spider.scheduler.config;

import com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesHandlerImpl;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by lq on 17-3-29.
 */
public class Chain {
    public static class Value{
        public Value(String scope,String modules){
            this.scope =scope;
            this.modules =modules;
        }
        private String scope;
        private String modules;

        public String getScope() {
            return scope;
        }

        public String getModules() {
            return modules;
        }
    }

    private String scope;
    private Map<Integer,Value> status;
    private Value prepare;
    private Value finished;

    public Chain(String pattern,String scope,Map<Integer,Value> status,Value prepare,Value finished) {
        this.scope = scope;
        this.status = status;
        this.prepare = prepare;
        this.finished = finished;
    }

    public String getScope() {
        return scope;
    }

    public Map<Integer, Value> getStatus() {
        return status;
    }

    public Value getPrepare() {
        return prepare;
    }

    public Value getFinished() {
        return finished;
    }
}

