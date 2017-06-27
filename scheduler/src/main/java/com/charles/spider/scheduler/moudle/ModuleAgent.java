package com.charles.spider.scheduler.moudle;

import com.alibaba.fastjson.JSON;
import com.charles.spider.common.moudle.Description;
import com.charles.spider.store.base.Query;
import com.charles.spider.store.condition.Condition;
import com.charles.spider.store.entity.Module;
import com.charles.spider.store.service.Service;
import com.google.common.base.Preconditions;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lq on 17-3-16.
 */
public class ModuleAgent {
    private static Map<String, ModuleCoreFactory> locks = new ConcurrentHashMap<String, ModuleCoreFactory>();

    private Path base = null;

    private Service<Module> service;


    public ModuleAgent(String basePath, Service<Module> service) throws IOException {
        this(Paths.get(basePath), service);
    }

    public ModuleAgent(Path path, Service<Module> service) {
        this.base = path;
        this.service = service;
    }


    private Path base() throws IOException {
        if (!Files.exists(this.base))
            Files.createDirectory(this.base);
        return base;
    }




    public Module get(String name) throws IOException {
        Path path = Paths.get(base().toString(), name);
        if (!Files.exists(path) || !Files.isDirectory(path))
            return null;

        Description desc = JSON.parseObject(
                Files.readAllBytes(Paths.get(path.toString(), "description")), Description.class);

        //return new Module(desc);
        return null;
    }


    public Module save(byte[] data, Description desc, boolean override) throws Exception {
        Path path = Paths.get(base().toString(), desc.getName());
        Path old = Paths.get(path.toString(), "data");
        Path tmp = Paths.get(path.toString(), "data.tmp");

        boolean exist = Files.exists(path);

        if (exist && !override) throw new FileAlreadyExistsException("the module is exist");

        if (!exist) Files.createDirectories(path);

        String hash = DigestUtils.sha1Hex(data);

        Module module = service.single(Query.Condition(Condition.where("name").is(desc.getName())));
        if (module == null)
            module = service.insert(toStoreEntity(desc, path, hash));

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

        if (count == 0) throw new Exception("unknow reason,the database can't update");


        return module;
    }

    private Module toStoreEntity(Description desc, Path path, String hash) {
        Module module = new Module();
        module.setPath(path.toString());
        module.setName(desc.getName());
        module.setDetail(desc.getDetail());
        module.setType(desc.getType());
        module.setUpdateTime(new Date());
        module.setHash(hash);
        module.setValid(false);

        return module;
    }

}
