package com.bh.spider.consistent.raft;

import java.io.IOException;
import java.util.Properties;

/**
 * @author liuqi19
 * @version $Id: App, 2019-04-02 13:40 liuqi19
 */
public class App {

    public static void main(String[] args) throws IOException {
        //建立raftNode
        Raft.Node node = new Raft.Node(100,new Properties());
        Raft raft = new Raft(node);

        //建立kv store




        //监听客户端端口


        //启动Raft

        raft.exec();

    }
}
