package com.charles.spider.scheduler.moudle;

import com.charles.spider.transfer.entity.ModuleType;
import com.charles.spider.transfer.entity.Module;
import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.charles.spider.store.base.Store;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;

/**
 * Created by lq on 17-3-16.
 */
public class ModuleAgent {

    private Path base = null;

    private ModuleType type = null;

    private Store store;


    public ModuleAgent(ModuleType type, Path path, Store store) {
        this.type = type;
        this.base = path;
        this.store = store;
    }


    protected Path base() {
        return base;
    }


    protected Store store() {
        return this.store;
    }


    public ModuleType type() {
        return this.type;
    }


    public Module get(String name) throws IOException {
        Path path = Paths.get(base().toString(), name);
        if (!Files.exists(path) || !Files.isDirectory(path))
            return null;

        Module module = store().single(Module.class, Query.Condition(Condition.where("name").is(name)));
        if (module == null) return null;
        if (module.getState() == Module.State.TMP) {
            Path old = Paths.get(path.toString(), "data");
            Path tmp = Paths.get(path.toString(), "data.tmp");
            Files.copy(tmp, old, StandardCopyOption.REPLACE_EXISTING);

            module.setState(Module.State.VALID);
            int count = store().update(module,
                    Condition.where("id").is(module.getId())
                            .and(Condition.where("update_time").is(module.getUpdateTime())));
            Files.delete(tmp);
        }

        return module;
    }


    public Module save(byte[] data, String name, ModuleType type, String description, boolean override) throws Exception {
        Path path = Paths.get(base().toString(), name);
        Path old = Paths.get(path.toString(), "data");
        Path tmp = Paths.get(path.toString(), "data.tmp");

        boolean exist = Files.exists(path);

        if (exist && !override) throw new FileAlreadyExistsException("the module is exist");

        if (!exist) Files.createDirectories(path);

        String hash = DigestUtils.sha1Hex(data);

        Module module = store().single(Module.class, Query.Condition(Condition.where("name").is(name)));
        if (module == null) {
            module = new Module();
            module.setPath(path.toString());
            module.setName(name);
            module.setDetail(description);
            module.setType(type);
            module.setUpdateTime(new Date());
            module.setHash(hash);
            module = (Module) store().insert(module).toObject();
        }

        if (module.getState() == Module.State.VALID && module.getHash().equals(hash))
            throw new ModuleNoChangeException();

        module.setHash(hash);

        //写入临时文件
        Files.write(tmp, data);
        module.setState(Module.State.TMP);
        store().update(module,
                Condition.where("id").is(module.getId())
                        .and(Condition.where("update_time").is(module.getUpdateTime())));

        Files.copy(tmp, old, StandardCopyOption.REPLACE_EXISTING);

        module.setState(Module.State.VALID);
        int count = store().update(module,
                Condition.where("id").is(module.getId())
                        .and(Condition.where("update_time").is(module.getUpdateTime())));

        if (count == 0) throw new Exception("unknown reason,the database can't update");

        Files.delete(tmp);

        return module;
    }


    public List<Module> select(Query query) {
        if (query == null) query = new Query();

        query.addCondition(Condition.where("type").is(type()));


        return store().select(Module.class, query);
    }


    public void delete(Query query) throws IOException {
        Module module = store().single(Module.class, query);

        if (module.getState() != Module.State.NULL) {
            int count = store().delete(Module.class, query);

            if (count == 1) {
                Path path = Paths.get(base().toString(), module.getName());
                FileUtils.deleteDirectory(path.toFile());
            }


        }
    }


    public Object object(String moduleName, String className) throws IOException, ModuleBuildException {
        return null;
    }


}
