package com.bh.spider.scheduler.component;

import com.bh.spider.common.component.Component;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public abstract class ComponentRepository {

    private Path base;
    private Component.Type componentType;

    private Metadata metadata;


    public ComponentRepository(Component.Type type, Path dir) throws IOException {

        this.base = dir;
        this.componentType = type;

        Files.createDirectories(dir);

        metadata = new Metadata(Paths.get(base.toString(), "metadata"));
    }


    public Component.Type componentType() {
        return componentType;
    }

    public Metadata metadata() {
        return metadata;
    }

    public Component get(String name) {
        return metadata().get(name);
    }


    public Component get(String name, boolean loadContent) throws IOException, CloneNotSupportedException {
        Component component = metadata().get(name);
        if (component != null && loadContent) {
            Component o = component.clone();

            Path path = Paths.get(base.toString(), join(component.getName(), componentType()));
            byte[] bytes = Files.readAllBytes(path);
            o.setData(bytes);

            return o;
        }
        return component;
    }


    public Component save(byte[] data, String name, String description, boolean override) throws Exception {
        return save(data, name, description, override, false);
    }


    public Component save(byte[] data, String name, String description, boolean override, boolean expired) throws IOException {
        Path old = Paths.get(base.toString(), join(name, componentType));
        Path tmp = Paths.get(base.toString(), name + ".tmp");

        Files.write(tmp, data);

        String hash = DigestUtils.sha1Hex(data);
        //写入元数据
        Component component = new Component(name, componentType());
        component.setDescription(description);
        component.setHash(hash);
        component.setExpired(expired);
        component.setCreateTime(new Date());
        metadata().write(component);

        Files.move(tmp, old, StandardCopyOption.REPLACE_EXISTING);
        return component;
    }

    public List<Component> all() {
        return metadata.components();
    }


    public void delete(String name) throws IOException {
        if (metadata().delete(name)) {
            Path old = Paths.get(base.toString(), name);
            Path tmp = Paths.get(base.toString(), name + ".tmp");
            if (Files.exists(old)) Files.delete(old);
            if (Files.exists(tmp)) Files.delete(tmp);
        }
    }

    public boolean waitFor(String name) throws InterruptedException {
        return metadata().waitFor(name);
    }


    public boolean waitFor(String name, long timeout) throws InterruptedException {
        return metadata().waitFor(name, timeout);

    }


    public Path basePath() {
        return base;
    }


    protected String join(String name, Component.Type type) {
        return Objects.toString(name, "") + (type == null ? "" : "." + type.text());
    }


    public abstract Class<?> loadClass(String name) throws IOException, ClassNotFoundException;

    public void reset(List<Component> components) {
        metadata.reset(components, true);
    }
}
