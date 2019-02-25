package com.bh.spider.ui.entity;

import com.bh.spider.common.member.Node;

import java.util.List;
import java.util.Map;

public class Profile {
    private Map<String,String> base;

    private List<Node> nodes;

    public Profile(){}

    public Profile(Map<String,String> base,List<Node> nodes){
        this.base = base;
        this.nodes = nodes;
    }

    public Map<String, String> getBase() {
        return base;
    }

    public void setBase(Map<String, String> base) {
        this.base = base;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}
