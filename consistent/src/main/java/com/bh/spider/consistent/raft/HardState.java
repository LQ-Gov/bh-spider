package com.bh.spider.consistent.raft;

/**
 * @author liuqi19
 * @version : HardState, 2019-04-23 17:12 liuqi19
 */
public class HardState {
    public final static HardState EMPTY=new HardState(0L,0,0L);
    private Long term;

    private Integer vote;

    private Long committed;


    public HardState(){}


    public HardState(Long term,Integer vote,Long committed){
        this.term = term;
        this.vote = vote;
        this.committed = committed;
    }


    public Long term(){return term;}

    public Integer vote(){return vote;}


    public Long committed(){return committed;}

}
