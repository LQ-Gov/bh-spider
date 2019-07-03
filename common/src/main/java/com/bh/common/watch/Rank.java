package com.bh.common.watch;

import java.util.ArrayList;

/**
 * @author liuqi19
 * @version Rank, 2019-07-02 16:12 liuqi19
 **/
public class Rank extends ArrayList<Rank.Item>  {

    private Rank(){
        this(10);


    }

    public Rank(int size){
        super(size);
    }




    public static class Item{
        private String name;

        private Number value;

        public Item(){}

        public Item(String name,Number value){
            this.name = name;
            this.value = value;
        }



        public String name(){return name;}

        public Number value(){return value;}



    }
}
