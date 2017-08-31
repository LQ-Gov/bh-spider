package com.bh.spider.scheduler.module;

import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.bh.spider.scheduler.persist.Service;
import com.bh.spider.transfer.entity.Component;
import com.bh.spider.transfer.entity.ModuleType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

public class ComponentProxy {

    private Path base;
    private Service<Component> service;
    private ModuleType componentType;

    public ComponentProxy(ModuleType type, Service<Component> service, Path basePath) {
        this.base = basePath;

        this.componentType = type;

        this.service = service;
    }

    protected Service<Component> service(){return service;}


    public Component get(String name) throws IOException {

        Path path = Paths.get(base.toString(), name);
        if (!Files.exists(path) || !Files.isDirectory(path))
            return null;
        Component component = service.single(Query.Condition(Condition.where("name").is(name)));
        if (component == null) return null;
        if (component.getState() == Component.State.TMP) {
            Path old = Paths.get(path.toString(), "data");
            Path tmp = Paths.get(path.toString(), "data.tmp");
            Files.copy(tmp, old, StandardCopyOption.REPLACE_EXISTING);

            component.setState(Component.State.VALID);
            int count = service.update(component,
                    Condition.where("id").is(component.getId())
                            .and(Condition.where("update_time").is(component.getUpdateTime())));
            Files.delete(tmp);
        }

        return component;
    }


    public Component save(byte[] data, String name, ModuleType type, String description, boolean override) throws Exception {
        Path path = Paths.get(base.toString(), name);
        Path old = Paths.get(path.toString(), "data");
        Path tmp = Paths.get(path.toString(), "data.tmp");

        boolean exist = Files.exists(path);

        if (exist && !override) throw new FileAlreadyExistsException("the component is exist");

        if (!exist) Files.createDirectories(path);

        String hash = DigestUtils.sha1Hex(data);

        Component component = service.single(Query.Condition(Condition.where("name").is(name)));
        if (component == null) {
            component = buildModule(name, type, path.toString(), hash, description);
            component = service.insert(component);
        }

        if (component.getState() == Component.State.VALID && component.getHash().equals(hash))
            throw new ComponentNoChangeException();

        component.setHash(hash);

        //写入临时文件
        Files.write(tmp, data);
        component.setState(Component.State.TMP);
        service.update(component,
                Condition.where("id").is(component.getId())
                        .and(Condition.where("update_time").is(component.getUpdateTime())));

        Files.copy(tmp, old, StandardCopyOption.REPLACE_EXISTING);

        component.setState(Component.State.VALID);
        int count = service.update(component,
                Condition.where("id").is(component.getId())
                        .and(Condition.where("update_time").is(component.getUpdateTime())));

        if (count == 0) throw new Exception("unknown reason,the database can't update");

        Files.delete(tmp);

        return component;
    }

    public List<Component> select(Query query) {
        if (query == null) query = new Query();

        query.addCondition(Condition.where("type").is(componentType));


        return service.select(query);
    }


    public Component delete(Query query) throws IOException {
        Component component = service.single(query);

        if (component !=null&& component.getState() != Component.State.NULL) {
            int count = service.delete(query);

            if (count == 1) {
                Path path = Paths.get(base.toString(), component.getName());
                FileUtils.deleteDirectory(path.toFile());
            }
        }
        return component;
    }

    protected Component buildModule(String name, ModuleType type, String path, String hash, String desc) {
        Component component = new Component();
        component.setName(name);
        component.setPath(path);
        component.setDetail(desc);
        component.setType(type);
        component.setUpdateTime(new Date());
        component.setHash(hash);
        return component;
    }
}
