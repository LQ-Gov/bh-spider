package com.bh.spider.rule;

import com.bh.spider.fetch.Extractor;

import java.util.Map;

public class ExtractorGroup {
    private Node[] chain;
    //private Map<String,String>

    public ExtractorGroup(){}


    private class Node{
        public Node(Extractor it){

        }


        public Node next(){
            return null;
        }
    }
}
