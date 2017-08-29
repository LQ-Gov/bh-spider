package com.charles.spider.scheduler.module;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class SpiderClassLoader extends URLClassLoader {
    private final LinkedHashMap<String, ClassLoader> loaders = new LinkedHashMap<>();

    //private Map<String,String> paths = new ConcurrentHashMap<>();

    private Set<String> paths = new HashSet<>();

    public SpiderClassLoader(ClassLoader parent) {
        super(new URL[]{}, parent);
    }


    public synchronized void addJar(Path path) throws MalformedURLException, FileNotFoundException {
        if (!Files.exists(path))
            throw new FileNotFoundException("the jar file not found");

        String key = path.toString();

        super.addURL(path.toUri().toURL());
    }
}
