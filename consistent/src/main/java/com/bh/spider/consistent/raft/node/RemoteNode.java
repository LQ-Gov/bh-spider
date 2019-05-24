package com.bh.spider.consistent.raft.node;

/**
 * @author liuqi19
 * @version : RemoteNode, 2019-05-23 11:39 liuqi19
 */
public class RemoteNode extends RaftNode {

    /**
     * 当前已append的索引的位置
     */
    private long index = -1;

    /**
     * 下一个需要复制的索引位置
     */
    private long next;




    public RemoteNode(Node node) {
        super(node);
    }




//    public boolean isPaused(){
//        return paused;
//    }
//
//
//
//    public void pause(){
//        paused=true;
//    }
//    /**
//     * 重新启动
//     */
//    public void resume(){
//        paused=false;
//    }














}
