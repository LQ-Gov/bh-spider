package com.charles.spider.scheduler.moudle;

import com.charles.spider.common.moudle.Description;
import com.charles.spider.common.moudle.ModuleType;
import com.charles.spider.scheduler.config.Config;
import com.charles.spider.store.base.Store;
import com.charles.spider.store.entity.Module;
import com.charles.spider.store.service.Service;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.NotSupportedException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.security.DigestException;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by lq on 17-3-16.
 */
public class ModuleCoreFactory {
    private static final Logger logger = LoggerFactory.getLogger(ModuleCoreFactory.class);

    private Map<ModuleType, ModuleAgent> agents = new HashMap<>();
    private static volatile ModuleCoreFactory obj = null;

    public ModuleCoreFactory(Service<Module> service) throws IOException {
        agents.put(ModuleType.JAR, new ModuleAgent(Paths.get(Config.INIT_DATA_PATH, "handler"), service));
        agents.put(ModuleType.CONFIG, new ModuleAgent(Paths.get(Config.INIT_DATA_PATH, "config"), service));
    }


    public ModuleEntity entity(byte[] data, Description desc) throws IOException, DigestException, ModuleNoChangeException {
        ModuleAgent agent = agents.get(desc.getType());

        return agent.entity(data, desc);


//        int len = ByteBuffer.wrap(data, 0, 4).getInt();
//
//        String name = new String(data, 4, len);
//
//        int pos = 4 + len;
//
//        String h = Hex.encodeHexString(hash(data, pos, len - pos));

//        Module module = manager.get(name);
//        if (module != null && h.equals((desc = module.getDescription()).getHash()))
//            throw new ModuleNoChangeException();
//
//        if(module==null||override) {
//            desc.setHash(h);
//            module = manager.save(name,data, pos, data.length - pos, desc);
//        }

        //logger.info("write module {} to path:{};override:{}", desc.getName(), override);
    }


    public static ModuleCoreFactory instance() throws IOException, NotSupportedException, SQLException, ClassNotFoundException {
        if (obj == null) {
            synchronized (ModuleCoreFactory.class) {
                if (obj == null) {

                    obj = new ModuleCoreFactory(Store.get(Config.INIT_STORE_DATABASE, System.getProperties()).module());
                }
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
