package com.bh.spider.client;

import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
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
public class ComponentOperation {
    private Client client = null;


    ComponentOperation(Client client) {
        this.client = client;
    }

    public void submit(Class<?> cls) throws Exception {
        submit(null, cls);
    }

    public void submit(String name, Class<?> cls) throws IOException {
        submit(name, cls, (String) null);
    }

    public void submit(String name, Class<?> cls, String desc) throws IOException {

        submit(name, cls,null, desc);

    }

    public void submit(String name, Class<?> cls, Component.Type type) throws IOException {
        submit(name, cls, type, null);
    }

    public void submit(String name, Class<?> cls, Component.Type type, String desc) throws IOException {

        Preconditions.checkArgument(cls != null, "you must special a valid class");
        Preconditions.checkArgument(type != null && type != Component.Type.UNKNOWN, "you must special a valid component type");


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


    public void submit(Path path) throws IOException {
        String name = path.getFileName().toString();
        name = FilenameUtils.getBaseName(name);
        submit(name, path);
    }


    public void submit(Path path, Component.Type type) throws IOException {
        submit(path, type, null);
    }

    public void submit(Path path, Component.Type type, String desc) throws IOException {
        String name = path.getFileName().toString();
        name = FilenameUtils.getBaseName(name);
        submit(name, path, type, desc);
    }

    public void submit(String name, Path path) throws IOException {
        submit(name, path, null);
    }
    public void submit(String name, Path path, String desc) throws IOException {
        String extension = FilenameUtils.getExtension(path.getFileName().toString());

        Component.Type type = Component.Type.EXTRACTOR;

        submit(name, path, type, desc);

    }

    public void submit(String name, String path, Component.Type type, String desc) throws IOException {
        submit(name, Paths.get(path), type, desc);
    }

    public void submit(String name, Path path, Component.Type type, String desc) throws IOException {

        assert path != null;

        Preconditions.checkArgument(type != null && type != Component.Type.UNKNOWN, "you must special a valid component type");
        Preconditions.checkArgument(Files.exists(path),"file not exists");

        String filename = path.getFileName().toString();

        name = name == null ? filename : name;

        byte[] data = Files.readAllBytes(path);

        client.write(CommandCode.SUBMIT_MODULE, null, data, name, type, desc);

    }


    public List<Component> select() {
        ParameterizedType type = ParameterizedTypeImpl.make(List.class, new Type[]{Component.class}, null);
        return client.write(CommandCode.GET_MODULE_LIST, type);
    }

    public Component get(String name,Component.Type type) {

        return client.write(CommandCode.GET_MODULE, Component.class, name,type);
    }

    public void delete(String name,Component.Type type) {
        client.write(CommandCode.DELETE_MODULE, null, name,type);
    }


}
