package com.bh.spider.ui.entity;

import com.bh.spider.ui.vo.NodeVo;

import java.util.List;
import java.util.Map;

public class Profile {
    private Map<String,Object> base;

    private List<NodeVo> nodes;

    public Profile(){}

    public Profile(Map<String,Object> base,List<NodeVo> nodes){
        this.base = base;
        this.nodes = nodes;
    }

    public Map<String, Object> getBase() {
        return base;
    }

    public void setBase(Map<String, Object> base) {
        this.base = base;
    }

    public List<NodeVo> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeVo> nodes) {
        this.nodes = nodes;
    }
}
