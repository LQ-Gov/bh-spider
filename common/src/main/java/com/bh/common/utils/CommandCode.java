package com.bh.common.utils;

public enum CommandCode {
    //COMPONENT相关操作
    SUBMIT_COMPONENT,//提交component
    GET_COMPONENT_LIST,//获取所有的component
    GET_COMPONENT,//获取单个component
    DELETE_COMPONENT,


    //RULE 相关操作
    SUBMIT_RULE,
    GET_RULE_LIST,//获取RULE列表
    GET_HOST_LIST,///获取所有规则的host
    DELETE_RULE,//删除rule(此删除并未真正删除，会进入一个清理阶段)
    TERMINATION_RULE,//真正删除rule的阶段

    SCHEDULER_RULE_EXECUTOR,//启动或暂停

    GET_RULE_RANK,



    PROFILE,
    GET_NODE_LIST,
    CONNECT,
    DISCONNECT,





    WORKER_GET_COMPONENT,
    WORKER_HEART_BEAT,

    HEARTBEAT,

    CHECK_COMPONENT_OPERATION_COMMITTED_INDEX,
    LOAD_OPERATION_ENTRY,
    WRITE_OPERATION_ENTRIES,


    RULE_FACADE,







    //request operation
    SUBMIT_REQUEST,//提交任务

    SUBMIT_REQUEST_BATCH,//批量提交任务

    GET_REQUEST_LIST,//查询请求列表


    LOAD_COMPONENT,
    LOAD_COMPONENT_ASYNC,

    WATCH,
    UNWATCH,
    GET_WATCH_POINT_LIST,

    /*******抓取命令*******/
    FETCH,//单个抓取
    FETCH_BATCH,//批量抓取

    REPORT,//正常抓取完成报告

    REPORT_EXCEPTION,//异常抓取完成报告

    CLEAR_EXPIRED_FETCH,//清理过期的fetcher


    LOG_STREAM,


    ALIVE,
    HEART_BEAT,
    TASK,
    PROCESS,
    CLOSE,


    SYNC_SERVER_LIST;




    private boolean needConsistent;


    CommandCode(){
        this.needConsistent=false;
    }

    CommandCode(boolean needConsistent){
        this.needConsistent = needConsistent;
    }



    public boolean needConsistent(){
        return needConsistent;
    }

}
