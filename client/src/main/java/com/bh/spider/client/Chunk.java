package com.bh.spider.client;

/**
 * @author liuqi19
 * @version Chunk, 2019-07-30 19:21 liuqi19
 **/
public class Chunk {
    private long id;
    private short code;
    private Object[] params;


    public Chunk(long id,short code,Object[] params){
        this.id =id;
        this.code = code;
        this.params = params;
    }


    public long id(){return id;}

    public short code(){return code;}

    public Object[] params(){return params;}
}
