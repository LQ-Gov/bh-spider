package com.bh.spider.scheduler.component;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

public class JarComponentClassLoader extends URLClassLoader {
    private ClassLoader parent;
    public JarComponentClassLoader(Path path, ClassLoader parent) throws MalformedURLException {
        super(new URL[]{path.toUri().toURL()}, parent);
        this.parent = parent;
    }
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            // 用户上传的jar包优先级最高
            Class<?> c = findLoadedClass(name);
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
        Class<?> cls = super.findClass(name);
        return cls;
    }

    public void refresh(long id){

    }
}
