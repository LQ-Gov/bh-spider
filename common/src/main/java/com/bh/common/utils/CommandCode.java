package com.bh.common.utils;

public enum CommandCode {
    //region COMPONENT相关操作

    SUBMIT_COMPONENT,//提交component
    GET_COMPONENT_LIST,//获取所有的component
    GET_COMPONENT,//获取单个component
    DELETE_COMPONENT,//删除组件
    COMPONENT_SNAPSHOT,//生成COMPONENT快照
    APPLY_COMPONENT_SNAPSHOT,//应用COMPONENT SNAP
    SYNC_COMPONENT_METADATA,//向worker同步METADATA
    SYNC_COMPONENT_OPERATION_ENTRIES,//向worker同步操作日志

    //endregion


    //region RULE 相关操作
    SUBMIT_RULE,//提交新的Rule
    GET_RULE_LIST,//获取RULE列表
    GET_RULE,//获取Rule内容
    EDIT_RULE,//编辑rule
    DELETE_RULE,//删除rule(此删除并未真正删除，会进入一个清理阶段)
    TERMINATION_RULE,//执行DELETE后进入的下一个动作,终止真正删除rule

    RULE_SNAPSHOT, //生成Rule快照
    APPLY_RULE_SNAPSHOT,//应用快照

    URL_COUNT,//统计URL的数量

    SCHEDULER_RULE_EXECUTOR,//启动或暂停

    GET_RULE_RANK,

    //endregion


    PROFILE,
    GET_NODE_LIST,
    CONNECT,
    DISCONNECT,


    WORKER_GET_COMPONENTS,
    WORKER_HEARTBEAT,

    HEARTBEAT,

    CHECK_COMPONENT_OPERATION_COMMITTED_INDEX,
    WRITE_OPERATION_ENTRIES,


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
    TASK,
    PROCESS,
    CLOSE,


    SYNC_SERVER_LIST,

    TEST;

}
