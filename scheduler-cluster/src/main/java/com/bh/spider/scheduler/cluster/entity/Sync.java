package com.bh.spider.scheduler.cluster.entity;

public class Sync {
    /**
     * 组件操作当前已commit的Index
     */
    private Long componentOperationCommittedIndex;
    /**
     * worker能承载的请求容量
     */
    private int capacity;

    public Long getComponentOperationCommittedIndex() {
        return componentOperationCommittedIndex;
    }

    public void setComponentOperationCommittedIndex(Long componentOperationCommittedIndex) {
        this.componentOperationCommittedIndex = componentOperationCommittedIndex;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
