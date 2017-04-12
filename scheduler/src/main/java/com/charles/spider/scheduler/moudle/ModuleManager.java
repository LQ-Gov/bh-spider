package com.charles.spider.scheduler.moudle;

import com.alibaba.fastjson.JSON;
import com.charles.spider.common.moudle.Description;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lq on 17-3-16.
 */
public class ModuleManager {
    private static Map<String,ModuleCoreFactory> locks = new ConcurrentHashMap<String, ModuleCoreFactory>();

    private Path basePath = null;

    public ModuleManager(String basePath) throws IOException {
        this(Paths.get(basePath));
    }

    public ModuleManager(Path path) throws IOException {
        this.basePath = path;
        if(!Files.exists(this.basePath))
            Files.createDirectory(this.basePath);


    }


    public Module get(String name) throws IOException {
        Path path = Paths.get(basePath.toString(), name);
        if (!Files.exists(path) || !Files.isDirectory(path))
            return null;

        Description desc = JSON.parseObject(
                Files.readAllBytes(Paths.get(path.toString(), "description")), Description.class);

        return new Module(desc);
    }


    public Module save(byte[] data,Description desc){
        return null;
    }






}
