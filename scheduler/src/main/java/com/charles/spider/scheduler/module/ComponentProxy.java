package com.charles.spider.scheduler.module;

import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.scheduler.persist.Service;
import com.charles.spider.transfer.entity.Module;
import com.charles.spider.transfer.entity.ModuleType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

public class ComponentProxy {

    private Path base;
    private Service<Module> service;
    private ModuleType componentType;

    public ComponentProxy(ModuleType type, Service<Module> service, Path basePath) {
        this.base = basePath;

        this.componentType = type;

        this.service = service;
    }

    protected Service<Module> service(){return service;}


    public Module get(String name) throws IOException {

        Path path = Paths.get(base.toString(), name);
        if (!Files.exists(path) || !Files.isDirectory(path))
            return null;
        Module module = service.single(Query.Condition(Condition.where("name").is(name)));
        if (module == null) return null;
        if (module.getState() == Module.State.TMP) {
            Path old = Paths.get(path.toString(), "data");
            Path tmp = Paths.get(path.toString(), "data.tmp");
            Files.copy(tmp, old, StandardCopyOption.REPLACE_EXISTING);

            module.setState(Module.State.VALID);
            int count = service.update(module,
                    Condition.where("id").is(module.getId())
                            .and(Condition.where("update_time").is(module.getUpdateTime())));
            Files.delete(tmp);
        }

        return module;
    }


    public Module save(byte[] data, String name, ModuleType type, String description, boolean override) throws Exception {
        Path path = Paths.get(base.toString(), name);
        Path old = Paths.get(path.toString(), "data");
        Path tmp = Paths.get(path.toString(), "data.tmp");

        boolean exist = Files.exists(path);

        if (exist && !override) throw new FileAlreadyExistsException("the module is exist");

        if (!exist) Files.createDirectories(path);

        String hash = DigestUtils.sha1Hex(data);

        Module module = service.single(Query.Condition(Condition.where("name").is(name)));
        if (module == null) {
            module = buildModule(name, type, path.toString(), hash, description);
            module = service.insert(module);
        }

        if (module.getState() == Module.State.VALID && module.getHash().equals(hash))
            throw new ComponentNoChangeException();

        module.setHash(hash);

        //写入临时文件
        Files.write(tmp, data);
        module.setState(Module.State.TMP);
        service.update(module,
                Condition.where("id").is(module.getId())
                        .and(Condition.where("update_time").is(module.getUpdateTime())));

        Files.copy(tmp, old, StandardCopyOption.REPLACE_EXISTING);

        module.setState(Module.State.VALID);
        int count = service.update(module,
                Condition.where("id").is(module.getId())
                        .and(Condition.where("update_time").is(module.getUpdateTime())));

        if (count == 0) throw new Exception("unknown reason,the database can't update");

        Files.delete(tmp);

        return module;
    }

    public List<Module> select(Query query) {
        if (query == null) query = new Query();

        query.addCondition(Condition.where("type").is(componentType));


        return service.select(query);
    }


    public Module delete(Query query) throws IOException {
        Module module = service.single(query);

        if (module!=null&& module.getState() != Module.State.NULL) {
            int count = service.delete(query);

            if (count == 1) {
                Path path = Paths.get(base.toString(), module.getName());
                FileUtils.deleteDirectory(path.toFile());
            }
        }
        return module;
    }

    protected Module buildModule(String name, ModuleType type, String path, String hash, String desc) {
        Module module = new Module();
        module.setName(name);
        module.setPath(path);
        module.setDetail(desc);
        module.setType(type);
        module.setUpdateTime(new Date());
        module.setHash(hash);
        return module;
    }
}
