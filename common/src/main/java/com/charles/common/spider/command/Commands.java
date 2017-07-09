package com.charles.common.spider.command;

/**
 * Created by lq on 17-3-16.
 */
public enum Commands {
    SUBMIT_REQUEST,//提交任务
    SUBMIT_MODULE,//提交处理模块
    SUBMIT_RULE,

    GET_MODULE_LIST,//获取module列表
    GET_RULE_LIST,//获取RULE列表

    GET_MODULE,//获取单个module

    DELETE_MODULE,


    ALIVE,
    TASK,
    PROCESS,
    REPORT,
    CLOSE
}
