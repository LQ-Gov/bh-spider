package com.bh.spider.scheduler;

import org.junit.jupiter.api.Test;

import java.net.Socket;

public class SocketTest {


    @Test
    public void test0(){
        Socket socket = new Socket();
        System.out.println(socket.isClosed());
    }
}
