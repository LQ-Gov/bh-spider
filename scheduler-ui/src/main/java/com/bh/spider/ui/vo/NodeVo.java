package com.bh.spider.ui.vo;

import com.bh.spider.common.member.Node;

/**
 * @author liuqi19
 * @version NodeVo, 2019-07-11 00:20 liuqi19
 **/
public class NodeVo {
    private String id;
    private String ip;
    private String hostname;
    private String os;
    private String type;
    private double CPUUtilization;
    private double memoryOccupancy;
    private double diskOccupancy;


    public NodeVo(Node node){
        this.setId(String.valueOf(node.getId()));
        this.setIp(node.getIp());
        this.setHostname(node.getHostname());
        this.setOs(node.getOs());
        this.setType(node.getType());
        this.setCPUUtilization(node.getCPUUtilization());
        this.setMemoryOccupancy(node.getMemoryOccupancy());
        this.setDiskOccupancy(node.getDiskOccupancy());
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
