package com.bh.spider.transfer;

public enum CommandCode {
    PROFILE,
    GET_NODE_LIST,
    CONNECT,


    //component operation
    SUBMIT_COMPONENT,//提交component
    GET_COMPONENT_LIST,//获取所有的component
    GET_COMPONENT,//获取单个component
    DELETE_COMPONENT,


    WORKER_GET_COMPONENT,
    WORKER_HEART_BEAT,

    CHECK_COMPONENT_OPERATION_COMMITTED_INDEX,
    LOAD_OPERATION_ENTRY,
    WRITE_OPERATION_ENTRIES,


    RULE_FACADE,


    //rule operation
    SUBMIT_RULE,
    GET_RULE_LIST,//获取RULE列表
    GET_HOST_LIST,///获取所有规则的host
    DELETE_RULE,//删除rule
    EDIT_RULE,//修改rule
    SCHEDULER_RULE_EXECUTOR,//启动或暂停




    //request operation
    SUBMIT_REQUEST,//提交任务

    GET_REQUEST_LIST,//查询请求列表


    LOAD_COMPONENT,
    LOAD_COMPONENT_ASYNC,

    WATCH,
    UNWATCH,

    /*******抓取命令*******/
    FETCH,//单个抓取
    FETCH_BATCH,//批量抓取

    REPORT,//正常抓取完成报告

    REPORT_EXCEPTION,//异常抓取完成报告


    ALIVE,
    HEART_BEAT,
    TASK,
    PROCESS,
    CLOSE;

}
