package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.moudle.Description;
import com.charles.spider.common.moudle.ModuleType;
import com.charles.spider.scheduler.config.Config;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.security.DigestException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lq on 17-3-16.
 */
public class ModuleCoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(ModuleCoreFactory.class);

    private Map<ModuleType, ModuleManager> managers = new HashMap<>();
    private static volatile ModuleCoreFactory obj = null;

    private ModuleCoreFactory() throws IOException {
        managers.put(ModuleType.HANDLE, new ModuleManager(Paths.get(Config.INIT_DATA_PATH, "handler")));
        managers.put(ModuleType.CONFIG, new ModuleManager(Paths.get(Config.INIT_DATA_PATH, "config")));
        managers.put(ModuleType.EXTEND, new ModuleManager(Paths.get(Config.INIT_DATA_PATH, "extend")));
    }


    public void save(byte[] data, Description desc, boolean override) throws IOException, DigestException, ModuleNoChangeException {
        ModuleManager manager = managers.get(desc.getType());

        int len = ByteBuffer.wrap(data, 0, 4).getInt();

        String name = new String(data, 4, len);

        int pos = 4 + len;

        String h = Hex.encodeHexString(hash(data, pos, len - pos));

        Module module = manager.get(name);
        if (module != null && h.equals((desc = module.getDescription()).getHash()))
            throw new ModuleNoChangeException();

        if(module==null||override) {
            desc.setHash(h);
            module = manager.save(name,data, pos, data.length - pos, desc);
        }

        logger.info("write module {} to path:{};override:{}", desc.getName(), override);
    }


    public static ModuleCoreFactory instance() throws IOException {
        if (obj == null) {
            synchronized (ModuleCoreFactory.class) {
                if (obj == null)
                    obj = new ModuleCoreFactory();
            }
        }
        return obj;
    }



    private static synchronized byte[] hash(byte[] data,int offset,int len) {
        MessageDigest digest = DigestUtils.getSha1Digest();
        digest.update(data, offset, len);
        return digest.digest();
    }









}