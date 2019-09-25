package com.bh.spider.common.member;

import com.sun.management.OperatingSystemMXBean;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class Node implements Comparable<Node> {

    private long id;
    private String ip;
    private String hostname;
    private String os;
    private String type;
    private double CPUUtilization;
    private double memoryOccupancy;
    private double diskOccupancy;


    public static Node self(String type) throws UnknownHostException {
        return self(0, type);
    }


    public static Node self(long id, String type) throws UnknownHostException {
        InetAddress local = Inet4Address.getLocalHost();

        Node node = new Node();
        node.setId(id);
        node.setIp(local.getHostAddress());
        node.setHostname(local.getHostName());
        node.setOs(System.getProperty("os.name"));
        node.setType(type);


        node.update();

        return node;
    }


    public static List<Node> collectionOf(Properties properties) {
        List<Node> list = new LinkedList<>();

        properties.forEach((k, v) -> {
            int id = Integer.parseInt(k.toString());

            URI uri = URI.create("addr://" + v.toString());
            Node node = new Node();
            node.setId(id);
            node.setIp(uri.getHost());
            list.add(node);
        });

        return list;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
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


    public void update() {
        OperatingSystemMXBean osmxb = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        this.setCPUUtilization(osmxb.getSystemCpuLoad());
        this.setMemoryOccupancy((1 - (osmxb.getFreePhysicalMemorySize() * 1.0) / osmxb.getTotalPhysicalMemorySize()));

        File[] rootFiles = File.listRoots();

        long totalSpace = Arrays.stream(rootFiles).map(File::getTotalSpace).reduce(0L, Long::sum);
        long usedSpace = Arrays.stream(rootFiles).map(File::getUsableSpace).reduce(0L, Long::sum);

        this.setDiskOccupancy(usedSpace * 1.0 / totalSpace);
    }

    public void update(Node node) {

    }

    @Override
    public int compareTo(Node o) {
        if (o == null) return 1;

        if (this.getId() == o.getId()) return 0;

        return this.getId() > o.getId() ? 1 : -1;
    }
}
