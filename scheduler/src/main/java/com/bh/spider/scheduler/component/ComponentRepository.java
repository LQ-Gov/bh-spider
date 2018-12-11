package com.bh.spider.scheduler.component;

import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.store.service.Service;
import com.bh.spider.transfer.entity.Component;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

public class ComponentRepository {

    private Path base;
    private Component.Type componentType;

    private Metadata metadata;


    public ComponentRepository(Component.Type type, Path dir) throws IOException {

        this.base = dir;
        this.componentType = type;

        metadata = new Metadata(Paths.get(base.toString(), "metadata"));
    }


    public Component.Type componentType(){
        return componentType;
    }

    public Metadata metadata(){
        return metadata;
    }

    public Component get(String name) throws IOException {
        return metadata.get(name);
    }


    public Component save(byte[] data, String name, String description, boolean override) throws Exception {
        Path old = Paths.get(base.toString(), name);
        Path tmp = Paths.get(base.toString(), name + ".tmp");

        Files.write(tmp, data);

        String hash = DigestUtils.sha1Hex(data);
        //写入元数据
        Component component = new Component(name, componentType());
        component.setDescription(description);
        component.setHash(hash);
        metadata().write(component);

        Files.move(tmp, old, StandardCopyOption.REPLACE_EXISTING);
        return component;
    }

    public List<Component> all() {
        return metadata.components();
    }


    public void delete(String name) throws IOException {
        if(metadata().delete(name)) {
            Path old = Paths.get(base.toString(), name);
            Path tmp = Paths.get(base.toString(), name + ".tmp");
            if (Files.exists(old)) Files.delete(old);
            if (Files.exists(tmp)) Files.delete(tmp);
        }
    }
}
