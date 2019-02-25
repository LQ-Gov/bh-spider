package com.bh.spider.common.member;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Node {
    private String ip;
    private String hostname;
    private String os;
    private String type;
    private String state;



    public static Node self() throws UnknownHostException {
        InetAddress local = Inet4Address.getLocalHost();

        Node node = new Node();
        node.setIp(local.getHostAddress());
        node.setHostname(local.getHostName());
        node.setOs(System.getProperty("os.name"));


        return node;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
