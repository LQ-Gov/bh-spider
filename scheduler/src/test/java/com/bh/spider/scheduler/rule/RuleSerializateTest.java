package com.bh.spider.scheduler.rule;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.quartz.SchedulerException;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

public class RuleSerializateTest {
    @Test
    public void test() {
        String pattern = "https://www.facebook.com";
        String pretty = pattern.replaceAll("/{2,}", "/");

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pretty);
        System.out.println(matcher.matches(Paths.get("https://www.facebook.com")));
    }
}
