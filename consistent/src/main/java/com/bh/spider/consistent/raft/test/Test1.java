package com.bh.spider.consistent.raft.test;

import com.bh.common.utils.ConvertUtils;
import com.bh.spider.consistent.raft.Actuator;
import com.bh.spider.consistent.raft.Raft;

import java.util.Timer;

/**
 * @author liuqi19
 * @version Test1, 2019-08-04 19:09 liuqi19
 **/
public class Test1 extends BaseTest {

    private static Timer timer = new Timer();

    public static void main(String[] args) throws Exception {
        Raft raft = initRaft(args);

        raft.bind(new TestActuator());



//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                byte[] bytes= ConvertUtils.toBytes( RandomUtils.nextInt());
//                raft.write(bytes);
//
//            }
//        },1000,1000);

        raft.exec().join();
    }



    private static class TestActuator implements Actuator{

        @Override
        public String name() {
            return null;
        }

        @Override
        public byte[] snapshot() {
            return new byte[0];
        }

        @Override
        public void recover(byte[] data) throws Exception {

        }

        @Override
        public void apply(byte[] entries) throws Exception {
            int value = ConvertUtils.toInt(entries);
            System.out.println("接收到值:"+value);

        }

        @Override
        public Object read(byte[] data, boolean wait) {
            return null;
        }
    }
}
