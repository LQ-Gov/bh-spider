package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : Vote, 2019-04-15 11:22 liuqi19
 */
public class Vote {
    private int id;

    private long term;

    public Vote(){}


    public Vote(int id,long term){
        this.id = id;
        this.term = term;
    }


    public int id(){return id;}


    public long term(){return term;}
}
