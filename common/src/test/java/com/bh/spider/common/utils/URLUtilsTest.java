package com.bh.spider.common.utils;

import com.bh.common.utils.URLUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author liuqi19
 * @version URLUtilsTest, 2019-06-03 11:18 liuqi19
 **/
public class URLUtilsTest {



    @Test
    public void formatUrlTest(){
        Assertions.assertEquals( URLUtils.format("/abc.html","http://","www.test.com"),"http://www.test.com/abc.html");

        Assertions.assertEquals( URLUtils.format("www.2h.com/abc.html","http://","www.test.com"),"http://www.2h.com/abc.html");


    }
}
