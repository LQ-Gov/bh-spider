package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.transport.Connection;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuqi19
 * @version : LocalNode, 2019-04-11 10:05 liuqi19
 */
public class LocalNode extends Node {

    private Map<Integer,Connection> connections = new HashMap<>();


    private NodeRole role;



    public LocalNode(Node node){
        super(node);
    }

    @Override
    public int id() {
        return 0;
    }

    NodeRole role(){return this.role;}


    void becomeFollower(){}

    void becomeCandidate(){}

    /**
     * 成为PRE-备选人
     */
    void becomePreCandidate(){}


    /**
     * 成为Leader
     */
    void becomeLeader(){}

//    @Override
//    public void sendTo(Node node, byte[] data) {
//        Connection conn = connections.get(node.id());
//        if (conn != null) {
//            conn.write(data);
//        }
//    }

    @Override
    public String hostname() {
        return null;
    }

    @Override
    public int port() {
        return 0;
    }


    public void bindConnection(Node node, Connection connection){
        connections.put(node.id(),connection);
    }


    public Connection connection(Node node){
        return connections.get(node.id());
    }


}
