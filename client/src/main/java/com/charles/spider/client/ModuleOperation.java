package com.charles.spider.client;

import com.charles.common.spider.command.Commands;
import com.charles.spider.common.moudle.Description;
import com.charles.spider.common.moudle.ModuleType;
import com.charles.spider.query.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
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

    public void submit(Class<?> cls, Description desc, boolean override) throws IOException {
        URL url = cls.getResource("");
        submit(url.getPath(), desc, override);
    }


    public void submit(String path) {
        submit(path, false);
    }

    public void submit(String path, boolean override) {
        submit(Paths.get(path), override);
    }


    public void submit(Path path) {
        submit(path, false);
    }

    public void submit(Path path, boolean override) {
    }

    /**
     * 提交module
     *
     * @param path
     * @param desc
     */
    public void submit(String path, Description desc, boolean override) throws IOException {
        submit(Paths.get(path), desc, override);
    }

    /**
     * 提交module
     *
     * @param path
     * @param desc
     */

    public void submit(Path path, Description desc, boolean override) throws IOException {
        assert desc != null;

        Preconditions.checkNotNull(desc, "the parameter of desc can't null");
        Preconditions.checkArgument(Files.exists(path), "the file isn't exist");


        if (StringUtils.isBlank(desc.getName()))
            desc.setName(path.getFileName().toString());

        if (desc.getType() == ModuleType.UNKNOWN) {
            int index = desc.getName().lastIndexOf('.');
            Preconditions.checkArgument(index > 0 && index < desc.getName().length(),
                    "can't analysis module type");

            String type = desc.getName().substring(index);
            desc.setType(ModuleType.valueOf(type));
        }


        byte[] data = Files.readAllBytes(path);


        client.write(Commands.SUBMIT_MODULE, null, data, desc, override);
    }


    public List<Description> select(Query query){


        ParameterizedType type = ParameterizedTypeImpl.make(List.class,new Type[]{Description.class},null);

        List<Description> result = client.write(Commands.GET_MODULE_LIST,type,query);

        return result;
    }


    public void delete(int id){
        client.write(Commands.DELETE_MODULE,null,id);
    }
}
