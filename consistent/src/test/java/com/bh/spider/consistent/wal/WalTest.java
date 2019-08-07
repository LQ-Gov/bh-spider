package com.bh.spider.consistent.wal;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuqi19
 * @version WalTest, 2019-08-05 14:59 liuqi19
 **/
public class WalTest {


    @Test
    public void test0(){
        String name ="0000000000000001-00000000000000c8.wal";

        Pattern WAL_NAME_PATTERN = Pattern.compile("");

        Matcher matcher = WAL_NAME_PATTERN.matcher(name);

        if(matcher.find()){
            System.out.println("hhhh");
        }

    }
}
