package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.moudle.Description;
import com.charles.spider.common.moudle.ModuleType;
import com.charles.spider.scheduler.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-3-16.
 */
public class ModuleCoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(ModuleCoreFactory.class);
    private static final Map<ModuleType,ModuleManager> managers = new HashMap<>();

    static {
        managers.put(ModuleType.HANDLE,new ModuleManager(Paths.get(Config.INIT_DATA_PATH,"handler")));
        managers.put(ModuleType.CONFIG,new ModuleManager(Paths.get(Config.INIT_DATA_PATH,"config")));
        managers.put(ModuleType.EXTEND,new ModuleManager(Paths.get(Config.INIT_DATA_PATH,"extend")));
    }

    public static void save(byte[] data,Description desc,boolean override) throws IOException {
        ModuleManager manager = managers.get(desc.getType());

        int len = ByteBuffer.wrap(data, 0, 4).getInt();

        String name = new String(data, 4, len);

        Module module = manager.get(name);
        if(module==null||override) {
            module = manager.save(data, desc);
            desc = module.getDescription();
            logger.info("write module {} to path:{};override:{}", desc.getName(), desc.getPath(), override);
        }
    }









}
