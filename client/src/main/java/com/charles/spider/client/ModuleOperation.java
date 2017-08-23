package com.charles.spider.client;

import com.charles.spider.transfer.CommandCode;
import com.charles.spider.transfer.entity.ModuleType;
import com.charles.spider.transfer.entity.Module;
import com.charles.spider.query.Query;
import com.charles.spider.query.condition.Condition;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lq on 7/9/17.
 */
public class ModuleOperation {
    private Client client = null;


    ModuleOperation(Client client) {
        this.client = client;
    }

    public void submit(Class<?> cls) throws Exception {
        submit(null, cls);
    }

    public void submit(String name, Class<?> cls) throws IOException {
        submit(name, cls, (String) null);
    }

    public void submit(String name, Class<?> cls, String desc) throws IOException {
        Preconditions.checkArgument(cls != null, "you must special a valid class");
        URL url = cls.getResource("");

        Preconditions.checkNotNull(url, "can't get path of %s", cls.getName());

        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + cls.getSimpleName() + ".*");
        System.out.println(url.getPath());
        System.out.println(Arrays.toString(new File(url.getPath()).list()));
        String[] paths = new File(url.getPath()).list((dir, name1) -> matcher.matches(dir.toPath()));


        Preconditions.checkState(paths != null && paths.length == 1, "this have multi class file");

        Arrays.stream(paths).forEach(System.out::println);

        name = name == null ? cls.getSimpleName() : name;

        submit(name, paths[0], desc);


    }

    public void submit(String name, Class<?> cls, ModuleType type) throws IOException {
        submit(name, cls, type, null);
    }


    public void submit(String name, Class<?> cls, ModuleType type, String desc) throws IOException {

        Preconditions.checkArgument(cls != null, "you must special a valid class");
        Preconditions.checkArgument(type != null && type != ModuleType.UNKNOWN, "you must special a valid module type");


        URL url = cls.getResource("");

        Preconditions.checkNotNull(url, "can't get path of %s", cls.getName());


        String path = url.getPath() + cls.getSimpleName();

        String extension = type.toString();

        if (!StringUtils.isBlank(extension)) {
            extension = StringUtils.lowerCase(extension);
            if (extension.startsWith(".")) path += extension;
            else path += "." + extension;
        }

        submit(name, path, type, desc);
    }


    public void submit(String path) throws IOException {
        submit(Paths.get(path));
    }

    public void submit(Path path) throws IOException {
        String name = path.getFileName().toString();
        name = FilenameUtils.getBaseName(name);
        submit(name, path);
    }


    public void submit(String name, String path) throws IOException {
        submit(name, path, null);
    }


    public void submit(String name, Path path) throws IOException {
        submit(name, path, null);
    }

    public void submit(String name, String path, String desc) throws IOException {

        submit(name, Paths.get(path), desc);

    }

    public void submit(String name, Path path, String desc) throws IOException {
        String extension = FilenameUtils.getExtension(path.getFileName().toString());

        ModuleType type = ModuleType.value(extension);

        submit(name, path, type, desc);


    }

    public void submit(String name, String path, ModuleType type, String desc) throws IOException {
        submit(name, Paths.get(path), type, desc);
    }

    public void submit(String name, Path path, ModuleType type, String desc) throws IOException {

        assert path != null;

        Preconditions.checkArgument(type != null && type != ModuleType.UNKNOWN, "you must special a valid module type");

        Preconditions.checkState(Files.exists(path), "file not exists");

        String filename = path.getFileName().toString();

        name = name == null ? filename : name;

        byte[] data = Files.readAllBytes(path);

        client.write(CommandCode.SUBMIT_MODULE, null, data, name, type, desc);

    }


    public List<Module> select(Query query) {


        ParameterizedType type = ParameterizedTypeImpl.make(List.class, new Type[]{Module.class}, null);


        return client.write(CommandCode.GET_MODULE_LIST, type, query);
    }


    public List<Module> select() {
        return select(null);
    }

    public Module get(String name) {
        List<Module> list = select(Query.Condition(Condition.where("name").is(name)));

        return list == null || list.isEmpty() ? null : list.get(0);
    }

    public void delete(Query query){
        client.write(CommandCode.DELETE_MODULE,null,query);
    }


}
