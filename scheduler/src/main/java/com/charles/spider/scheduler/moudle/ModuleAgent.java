package com.charles.spider.scheduler.moudle;

import com.alibaba.fastjson.JSON;
import com.charles.spider.common.moudle.Description;
import com.charles.spider.store.entity.Module;
import com.charles.spider.store.service.Service;
import com.google.common.base.Preconditions;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
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
    private static Map<String,ModuleCoreFactory> locks = new ConcurrentHashMap<String, ModuleCoreFactory>();

    private Path base = null;

    private Service<Module> service;






    public ModuleAgent(String basePath, Service<Module> service) throws IOException {
        this(Paths.get(basePath),service);
    }

    public ModuleAgent(Path path,Service<Module> service) {
        this.base = path;
        this.service = service;
    }


    private Path base() throws IOException {
        if (!Files.exists(this.base))
            Files.createDirectory(this.base);
        return base;
    }


    public ModuleEntity entity(byte[] data,Description desc) {
        return new ModuleEntity(this, data, desc);
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


    public Module save(String name, byte[] data,Description desc,boolean override) throws IOException {
        Path path = Paths.get(base().toString(), name);

        Preconditions.checkArgument(Files.exists(path) && !override, "the module of {} is exists", path);

        String hash = DigestUtils.sha1Hex(data);


        //写入临时文件
        Files.write(Paths.get(path.toString(), "data.tmp"), data);


        Module module = toEntity(desc, path, hash);


        module = service.save(module);

        //delete old version,move new version


        Files.delete(Paths.get(path.toString(), "data"));
        Files.move(Paths.get(path.toString(), "data.tmp"), Paths.get(path.toString(), "data"));


        return module;
    }

    private Module toEntity(Description desc,Path path,String hash){
        Module module = new Module();
        module.setPath(path.toString());
        module.setName(desc.getName());
        module.setDetail(desc.getDetail());
        module.setType(desc.getType());
        module.setUpdateTime(new Date());
        module.setHash(hash);

        return module;

    }


//    public Module save (String name, byte[] data, int offset, int len, Description desc) throws IOException {
//        Path path = Paths.get(base().toString(), name);
//        if (!Files.exists(path))
//            Files.createDirectory(path);
//        FileOutputStream s1 = new FileOutputStream(new File(path.toString(), name+".tmp"));
//        s1.write(data, offset, len);
//        s1.flush();
//
//        FileOutputStream s2 = new FileOutputStream(new File(path.toString(), "description.tmp"));
//        s2.write(JSON.toJSONString(desc).getBytes());
//        s2.flush();
//
//        //return new Module(desc);
//        return null;
//    }






}
