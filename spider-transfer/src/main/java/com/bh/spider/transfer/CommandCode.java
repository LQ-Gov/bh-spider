package com.bh.spider.transfer;

public enum CommandCode {
    //component operation
    SUBMIT_COMPONENT,//提交component
    GET_COMPONENT_LIST,//获取所有的component
    GET_COMPONENT,//获取单个component
    DELETE_COMPONENT,


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

    WATCH,

    FETCH,

    FETCH_BATCH,//批量抓取


    ALIVE,
    TASK,
    PROCESS,
    REPORT,
    CLOSE;

}
