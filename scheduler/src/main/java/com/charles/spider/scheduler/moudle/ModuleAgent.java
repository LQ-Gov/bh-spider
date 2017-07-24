package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.constant.ModuleTypes;
import com.charles.spider.common.extractor.Extractor;
import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.common.entity.Module;
import com.charles.spider.store.service.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lq on 17-3-16.
 */
public class ModuleAgent<T> {
    private static Map<String, Object> extractorObjects = new ConcurrentHashMap<>();

    private Path base = null;

    private ModuleTypes type = null;

    private Service<Module> service;


    public ModuleAgent(ModuleTypes type, String basePath, Service<Module> service) throws IOException {
        this(type, basePath == null ? null : Paths.get(basePath), service);
    }

    public ModuleAgent(ModuleTypes type, Path path, Service<Module> service) {
        this.type = type;
        this.base = path;
        this.service = service;
    }


    private Path base() throws IOException {
        if (!Files.exists(this.base))
            Files.createDirectory(this.base);
        return base;
    }


    protected Service<Module> service() {
        return this.service;
    }


    public ModuleTypes type() {
        return this.type;
    }


    public Module get(String name) throws IOException {
        Path path = Paths.get(base().toString(), name);
        if (!Files.exists(path) || !Files.isDirectory(path))
            return null;

        return service.single(Query.Condition(Condition.where("name").is(name)));
    }


    public Module save(byte[] data, String name, ModuleTypes type, String description, boolean override) throws Exception {
        Path path = Paths.get(base().toString(), name);
        Path old = Paths.get(path.toString(), "data");
        Path tmp = Paths.get(path.toString(), "data.tmp");

        boolean exist = Files.exists(path);

        if (exist && !override) throw new FileAlreadyExistsException("the module is exist");

        if (!exist) Files.createDirectories(path);

        String hash = DigestUtils.sha1Hex(data);

        Module module = service.single(Query.Condition(Condition.where("name").is(name)));
        if (module == null) {
            module = new Module();
            module.setPath(path.toString());
            module.setName(name);
            module.setDetail(description);
            module.setType(type);
            module.setUpdateTime(new Date());
            module.setHash(hash);
            module.setValid(false);
            module = service.insert(module);
        }

        if (module.isValid() && module.getHash().equals(hash)) throw new ModuleNoChangeException();

        //写入临时文件
        Files.write(tmp, data);

        //delete old version,move new version
        if (Files.exists(old)) Files.delete(old);
        Files.move(tmp, old);


        module.setHash(hash);
        module.setValid(true);
        int count = service.update(module,
                Condition.where("id").is(module.getId())
                        .and(Condition.where("update_time").is(module.getUpdateTime())));

        if (count == 0) throw new Exception("unknown reason,the database can't update");


        return module;
    }


    public List<Module> select(Query query) {
        if (query == null) query = new Query();

        query.addCondition(Condition.where("type").is(type()));


        return service.select(query);
    }


}
