package com.bh.spider.consistent.raft.node;

/**
 * @author liuqi19
 * @version : Node, 2019-04-10 19:50 liuqi19
 */
public class Node {
    private int id;
    private String hostname;
    private int port;
    private long index=-1;

    private long next;

    private boolean paused = false;



    public Node(Node node){
        this.id = node.id();
        this.hostname = node.hostname();
        this.port = node.port();
    }



    public Node(int id,String hostname,int port){
        this.id = id;
        this.hostname = hostname;
        this.port = port;
    }

    public int id(){
        return id;
    }

    public String hostname(){
        return hostname;
    }

    public int port(){
        return port;
    }


    public boolean advance(long index){
        if(this.index<index)
            this.index = index;


        return true;

    }

    public long index(){
        return index;
    }


    /**
     * 变为复制者
     */
    public void becomeProbe(){

    }


    public boolean isPaused(){
        return paused;
    }



    public void pause(){
        paused=true;
    }
    /**
     * 重新启动
     */
    public void resume(){
        paused=false;
    }


    public long next(){
        return next;
    }



    

}
