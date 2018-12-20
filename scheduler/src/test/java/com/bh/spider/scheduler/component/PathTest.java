package com.bh.spider.scheduler.component;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathTest {


    @Test
    public void test() throws IOException {
        Path path1 = Paths.get("data/rule");
        Path path2 = Paths.get("data/../data/rule222");


        FilenameUtils.equals(path1.toString(),path2.toString());

        System.out.println( path1.equals(path2));
        System.out.println(path2.toFile().getCanonicalPath());
    }
}
