package com.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.AntPathMatcher;

public class AntPathTest {
    private AntPathMatcher pathMatcher;
    @BeforeEach
    public void before(){
        this.pathMatcher = new AntPathMatcher();
    }

    @Test
    public void test(){

        Assertions.assertTrue(pathMatcher.match("/a6654**/", "/a6654061509418680835/"));

    }
}
