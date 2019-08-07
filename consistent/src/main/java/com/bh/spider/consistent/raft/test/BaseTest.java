package com.bh.spider.consistent.raft.test;

import com.bh.spider.consistent.raft.DefaultActuator;
import com.bh.spider.consistent.raft.Raft;
import com.bh.spider.consistent.raft.node.Node;

import java.util.Properties;

/**
 * @author liuqi19
 * @version BaseTest, 2019-08-04 19:09 liuqi19
 **/
public class BaseTest {


    public static Raft initRaft(String[] args) throws Exception {
        Node[] nodes = new Node[3];
        nodes[0] = new Node(1,"127.0.0.1",9930);
        nodes[1] = new Node(2,"127.0.0.1",9931);
        nodes[2] = new Node(3,"127.0.0.1",9932);



        int index = Integer.valueOf( args[0])-1;


        Node local = nodes[index];

        Node[] members = new Node[nodes.length-1];


        for(int i=0,ni=0;i<nodes.length;i++) {
            if (i == index) continue;

            members[ni++] = nodes[i];

        }


        //建立kv store(状态机)




        //监听客户端端口


        //启动Raft

        Properties properties = new Properties();
        properties.put("wal.path","data/wal-"+local.id());
        properties.put("snapshot.path","data/snap-"+local.id());

        Raft raft =new Raft(properties,new DefaultActuator(),local,members);

        return raft;


    }
}
