package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : MessageType, 2019-04-08 14:16 liuqi19
 */
public enum  MessageType {


    /**
     * 节点连接
     */
    ACTIVE,

    /**
     * 节点断开连接
     */
    INACTIVE,

    //region 几个基本常用消息类型
    /**
     * 客户端发往到集群的写请求是通过MsgProp消息表示的
     */
    PROP,
    /**
     * 当一个节点通过选举成为Leader时，会发送MsgApp消息
     */
    APP,
    /**
     * MsgApp的响应消息
     */
    APP_RESP,

    /**
     * Leader发送的心跳消息
     */
    HEARTBEAT,

    /**
     * Follower处理心跳回复返回的消息类型
     */
    HEARTBEAT_RESP,

    /**
     * Leader向Follower发送快照信息
     */
    SNAP,


    //endregion

    /**
     * 当PreCandidate状态节点收到半数以上的投票之后，会发起新一轮的选举，即向集群中的其他节点发送MsgVote消息
     */
    VOTE,

    /**
     * MsgVote选举消息响应的消息
     */
    VOTE_RESP,






    /**
     * Follower消息不可达
     */
    UNREACHABLE,

    /**
     * Leader节点转移时使用，本地消息
     */
    TRANSFER_LEADER,
    /**
     * Leader节点转移超时，会发该类型的消息，使Follower的选举计时器立即过期，并发起新一轮的选举
     */
    TIMEOUT_NOW,
    /**
     * 客户端发往集群的只读消息使用MsgReadIndex消息（只读的两种模式：ReadOnlySafe和ReadOnlyLeaseBased）
     */
    READ_INDEX,

    /**
     * MsgReadIndex消息的响应消息
     */
    READ_INDEX_RESP,
}
