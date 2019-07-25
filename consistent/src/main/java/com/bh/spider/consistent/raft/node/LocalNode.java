package com.bh.spider.consistent.raft.node;

import com.bh.spider.consistent.raft.Message;
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
public class LocalNode extends RaftNode {
    private final static Logger logger = LoggerFactory.getLogger(LocalNode.class);

    private final Role[] ROLE_CACHE;

    private Map<Integer, Connection> connections = new HashMap<>();


    private Role role;


    public LocalNode(Node node,Follower follower,Candidate candidate,PreCandidate preCandidate,Leader leader) {
        super(node);
        this.role = new Assister();

        this.ROLE_CACHE = new Role[]{follower, candidate, preCandidate, leader};

        this.active(true);

    }
    @Override
    public int id() {
        return super.id();
    }

    public RoleType role() {
        return role == null ? null : this.role.name();
    }


    public Role role2(){
        return role;
    }


    public void becomeFollower() {
        role = ROLE_CACHE[0];
    }

    public void becomeCandidate() {
        role = ROLE_CACHE[1];
    }
    /**
     * 成为PRE-备选人
     */
    public void becomePreCandidate() {
        role = ROLE_CACHE[2];
    }
    /**
     * 成为Leader
     */
    public void becomeLeader() {
        role = ROLE_CACHE[3];
    }

    public void sendTo(RaftNode node, Message message){
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
}
