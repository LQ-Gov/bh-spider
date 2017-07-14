package com.charles.spider.client;

import com.charles.spider.common.command.Commands;
import com.charles.spider.common.constant.ModuleTypes;
import com.charles.spider.common.entity.Module;
import com.charles.spider.query.Query;
import com.google.common.base.Preconditions;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by lq on 7/9/17.
 */
public class ModuleOperation {
    private Client client = null;


    ModuleOperation(Client client){
        this.client = client;
    }

    public void submit(Class<?> cls) {
        submit(cls, false);
    }

    public void submit(Class<?> cls, boolean override) {
        submit(cls, override);
    }



    public void submit(Path path) throws IOException {
        submit(path.toString(),null, false);
    }


    public void submit(String path,boolean override) throws IOException {
        submit(path,null,override);
    }


    public void submit(String path,String name,boolean override) throws IOException {
        submit(path, name, (ModuleTypes) null, override);
    }



    public void submit(String path, String name, ModuleTypes type, boolean override) throws IOException {
        submit(Paths.get(path),name,type,null,override);
    }

    /**
     * 提交module
     *
     * @param path
     * @param desc
     */
    public void submit(String path,String name,String desc, boolean override) throws IOException {
        submit(Paths.get(path), name, null, desc, override);
    }

    public void submit(Path path, String name, ModuleTypes type, String description, boolean override) throws IOException {
        assert path != null;

        Preconditions.checkState(Files.exists(path), "file not exists");

        String filename = path.getFileName().toString();

        name = name == null ? filename : name;

        type = type == null ? ModuleTypes.valueOf(filename.substring(filename.lastIndexOf('.') + 1)) : type;

        byte[] data = Files.readAllBytes(path);

        client.write(Commands.SUBMIT_MODULE,null,data,name,type,description,override);

    }


    public List<Module> select(Query query) {


        ParameterizedType type = ParameterizedTypeImpl.make(List.class, new Type[]{Module.class}, null);


        return client.write(Commands.GET_MODULE_LIST, type, query);
    }


    public List<Module> select(){
        return  select(null);
    }


    public void delete(int id){
        client.write(Commands.DELETE_MODULE,null,id);
    }
}
