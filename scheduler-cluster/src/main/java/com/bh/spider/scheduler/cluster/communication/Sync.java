package com.bh.spider.scheduler.cluster.communication;

public class Sync {
    /**
     * 组件操作当前已commit的Index
     */
    private Long componentOperationCommittedIndex;
    /**
     * worker能承载的请求容量
     */
    private int capacity;


    private double CPUUtilization;

    private double memoryOccupancy;

    private double diskOccupancy;



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


    public double getCPUUtilization() {
        return CPUUtilization;
    }

    public void setCPUUtilization(double CPUUtilization) {
        this.CPUUtilization = CPUUtilization;
    }

    public double getMemoryOccupancy() {
        return memoryOccupancy;
    }

    public void setMemoryOccupancy(double memoryOccupancy) {
        this.memoryOccupancy = memoryOccupancy;
    }

    public double getDiskOccupancy() {
        return diskOccupancy;
    }

    public void setDiskOccupancy(double diskOccupancy) {
        this.diskOccupancy = diskOccupancy;
    }
}
