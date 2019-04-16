package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : MessageType, 2019-04-08 14:16 liuqi19
 */
public enum  MessageType {
    /**
     * 连接消息
     */
    CONNECT,
    /**
     * 当Follower节点的选举计时器超时，会发送MsgHup消息
     */
    HUP,
    /**
     * Leader发送心跳，主要作用是探活，Follower接收到MsgBeat会重置选举计时器，防止Follower发起新一轮选举
     */
    BEAT,
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
     * 当PreCandidate状态节点收到半数以上的投票之后，会发起新一轮的选举，即向集群中的其他节点发送MsgVote消息
     */
    VOTE,

    /**
     * MsgVote选举消息响应的消息
     */
    VOTE_RESP,
    /**
     * Leader向Follower发送快照信息
     */
    SNAP,

    /**
     * Leader发送的心跳消息
     */
    HEARTBEAT,

    /**
     * Follower处理心跳回复返回的消息类型
     */
    HEARTBEAT_RESP,

    /**
     * Follower消息不可达
     */
    UNREACHABLE,

    /**
     * 如果Leader发送MsgSnap消息时出现异常，则会调用Raft接口发送MsgUnreachable和MsgSnapStatus消息
     */
    SNAP_STATUS,

    /**
     * Leader检测是否保持半数以上的连接
     */
    CHECK_QUORUM,

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

    /**
     * PreCandidate状态下的节点发送的消息
     */
    PRE_VOTE,

    /**
     * 预选节点收到的响应消息
     */
    PRE_VOTE_RESP,
}
