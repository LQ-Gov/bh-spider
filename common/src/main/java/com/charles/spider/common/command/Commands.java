package com.charles.spider.common.command;

/**
 * Created by lq on 17-3-16.
 */
public enum Commands {

    //module operation
    SUBMIT_MODULE,//提交处理模块
    GET_MODULE_LIST,//获取module列表
    GET_MODULE,//获取单个module
    DELETE_MODULE,


    //rule operation
    SUBMIT_RULE,
    GET_RULE_LIST,//获取RULE列表
    GET_HOST_LIST,///获取所有规则的host
    DELETE_RULE,//删除rule
    EDIT_RULE,//修改rule
    SCHEDULER_RULE_EXECUTOR,//启动或暂停



    //request operation
    SUBMIT_REQUEST,//提交任务

    FETCH,


    ALIVE,
    TASK,
    PROCESS,
    REPORT,
    CLOSE
}
