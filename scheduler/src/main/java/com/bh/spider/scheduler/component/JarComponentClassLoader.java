package com.bh.spider.scheduler.component;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class JarComponentClassLoader extends URLClassLoader {
    private ClassLoader parent;


    private final Map<String, Class<?>> classCache = Collections.synchronizedMap(new WeakHashMap<>());

    private Map<String, ClassLoader> classLoaders = new HashMap<>();

    public JarComponentClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
        this.parent = parent;


    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 用户上传的jar包优先级最高
            Class<?> c = classCache.get(name);
            if (c == null) {
                c = findClass(name);
                if (c == null) {
                    c = parent.loadClass(name);
                }
            }
            return c;
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {

        //Class<?> cls = super.findClass(name);


        for (ClassLoader classLoader : classLoaders.values()) {
            Class<?> cls = classLoader.loadClass(name);
            if (cls.getClassLoader() == classLoader) {
                classCache.put(name, cls);
            }

            return cls;

        }
        return null;
    }


    public void addJar(String path) throws IOException {
        addJar(Paths.get(path));

    }

    public void addJar(Path path) throws IOException {
        String p = path.toFile().getCanonicalPath();
        classLoaders.put(p, new URLClassLoader(new URL[]{new URL("file://"+p)}, parent));
    }

    public void removeJar(String path) throws IOException {
        removeJar(Paths.get(path));
    }

    public void removeJar(Path path) throws IOException {
        String p = path.toFile().getCanonicalPath();
        classLoaders.remove(p);
    }
}
