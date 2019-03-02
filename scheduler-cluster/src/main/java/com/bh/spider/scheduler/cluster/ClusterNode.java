package com.bh.spider.scheduler.cluster;

import com.bh.spider.common.member.Node;

public class ClusterNode extends Node {
    /**
     * 组件的操作日志索引
     */
    private boolean alive;
    private long componentOperationCommittedIndex;

    /**
     * 节点的承载量
     */
    private int capacity;


    public ClusterNode(){}

    public ClusterNode(Node node){
        this.setHostname(node.getHostname());
        this.setIp(node.getIp());
        this.setOs(node.getOs());
        this.setType(node.getType());
    }


    public long getComponentOperationCommittedIndex() {
        return componentOperationCommittedIndex;
    }

    public void setComponentOperationCommittedIndex(long componentOperationCommittedIndex) {
        this.componentOperationCommittedIndex = componentOperationCommittedIndex;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
