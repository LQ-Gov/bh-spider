package com.bh.spider.consistent.raft;

import com.bh.spider.consistent.raft.pb.Entry;
import com.bh.spider.consistent.raft.pb.Snapshot;
import com.bh.spider.consistent.raft.snap.Snapshotter;
import com.bh.spider.consistent.raft.storage.MemoryStorage;
import com.bh.spider.consistent.raft.storage.Storage;
import com.bh.spider.consistent.raft.wal.WAL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.Timer;

/**
 * @author liuqi19
 * @version $Id: Raft, 2019-04-02 13:41 liuqi19
 */
public class Raft {
    private Node node;

    private Timer timer = new Timer();


    private Tick tick;

    public Raft(Node node){
        this.node = node;
    }



    public void exec() throws IOException {
        //创建快照目录
        Files.createDirectories(node.snapPath);
        node.snapshotter = Snapshotter.create(node.snapPath);

        Snapshot snapshot = node.snapshotter.load();

        if(!Files.exists(node.snapPath)) {
            //创建wal
            WAL.create(node.walPath, null);
        }

        WAL wal = WAL.open(node.walPath,null);


        Storage storage = new MemoryStorage();
        if(snapshot!=null)
            storage.applySnapshot(snapshot);

        List<Entry> entries= wal.readAll();

        node.lastIndex= entries.get(entries.size()-1).getIndex();

        storage.append(entries);




        //建立通信通道

        //启动


        timer.schedule(new Ticker(this),0,100);


    }


    /**
     * 成为跟随者
     * @param term
     * @param leader
     */
    public void becomeFollower(long term,Node leader){

    }


    /**
     * 成为备选人
     */
    public void becomeCandidate(){}


    /**
     * 成为PRE-备选人
     */
    public void becomePreCandidate(){}


    /**
     * 成为Leader
     */
    public void becomeLeader(){}


    public void tick(){

    }



    public static class Node{
        private int id;
        private Path snapPath;
        private Path walPath;

        private Snapshotter snapshotter;


        private long lastIndex;


        public Node(int id, Properties properties){
            this.id = id;
        }

    }
}
