package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.role.*;
import com.bh.spider.consistent.raft.transport.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuqi19
 * @version : LocalNode, 2019-04-11 10:05 liuqi19
 */
public class LocalNode extends Node {
    private final static Logger logger = LoggerFactory.getLogger(LocalNode.class);

    private Role[] roleCache;

    private Map<Integer, Connection> connections = new HashMap<>();


    private Role role;


    public LocalNode(Raft raft, Node node) {
        super(node);


        roleCache = new Role[]{
                new Follower(raft, this),
                new Candidate(raft, this),
                new PreCandidate(),
                new Leader()};
    }

    @Override
    public int id() {
        return super.id();
    }

    NodeRole role() {
        return role == null ? null : this.role.name();
    }


    void becomeFollower() {
        role = roleCache[0];
    }

    void becomeCandidate() {
        role = roleCache[1];
    }

    /**
     * 成为PRE-备选人
     */
    void becomePreCandidate() {
        role = roleCache[2];
    }


    /**
     * 成为Leader
     */
    void becomeLeader() {
        role = roleCache[3];
    }

//    @Override
//    public void sendTo(Node node, byte[] data) {
//        Connection conn = connections.get(node.id());
//        if (conn != null) {
//            conn.write(data);
//        }
//    }


    public void sendTo(Node node,Message message){
        Connection conn = connections.get(node.id());
        if(conn!=null){
            conn.write(message);
        }
    }

    @Override
    public String hostname() {
        return super.hostname();
    }

    @Override
    public int port() {
        return super.port();
    }


    public void bindConnection(Node node, Connection connection) {
        connections.put(node.id(), connection);

    }

    public void commandHandler(Message message) {
        Role r = this.role;
        if (r != null) {
            r.handler(message);
        }
    }


}
