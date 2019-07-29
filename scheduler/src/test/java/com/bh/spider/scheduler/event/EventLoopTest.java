package com.bh.spider.scheduler.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.quartz.SchedulerException;

import java.util.concurrent.CompletableFuture;

/**
 * @author liuqi19
 * @version EventLoopTest, 2019-07-29 13:10 liuqi19
 **/
public class EventLoopTest {

    private EventLoop eventLoop;


    private TestAssistant1 assistant1 = new TestAssistant1();
    private TestAssistant2 assistant2 = new TestAssistant2();
    private TestAssistant3 assistant3 = new TestAssistant3();


    @BeforeEach
    public void before() throws SchedulerException {
        eventLoop = new EventLoop(assistant1, assistant2, assistant3);

    }


    @Test
    public void test0() {
        int size =100_00000;
        CompletableFuture[] futures = new CompletableFuture[size];
        TestContext ctx = new TestContext();
        for (int i = 0; i < size; i++) {
            Command command = new Command(ctx, "TEST1");
            futures[i] = eventLoop.execute(command);
        }

        CompletableFuture.allOf(futures).join();

        System.out.println( assistant1.i);

    }


    @Test
    public void test1() {
        String[] keys = new String[]{"TEST1", "TEST2", "TEST3"};

        int size =100_00000;
        CompletableFuture[] futures = new CompletableFuture[size];

        TestContext ctx = new TestContext();

        for (int i = 0; i < 100_00000; i++) {
            Command command = new Command(ctx, keys[i % 3]);
            futures[i] = eventLoop.execute(command);
        }

         CompletableFuture.allOf(futures).join();

        System.out.println(assistant1.i);
        System.out.println(assistant2.i);
        System.out.println(assistant3.i);
        System.out.println(assistant1.i+assistant2.i+assistant3.i);
    }
}
