package com.bh.spider.client;

import com.bh.spider.transfer.CommandCode;
import com.bh.spider.transfer.entity.Component;
import com.bh.spider.query.Query;
import com.bh.spider.query.condition.Condition;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.*;
import java.rmi.server.ExportException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by lq on 7/9/17.
 */
public class ComponentOperation {
    private Client client = null;

    private String classBasePath;


    ComponentOperation(Client client, Properties properties) {
        this.client = client;
        classBasePath = properties.getProperty("class.file.base.path");
    }

    public void submit(Class<?> cls) throws Exception {
        submit(cls,null);
    }

    public void submit(Class<?> cls,Component.Type type) throws IOException {
        submit(cls,type, null);
    }

    public void submit(Class<?> cls,Component.Type type,String desc) throws IOException {
        submit(null,cls,type,desc);
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

        String clsPath = String.join(File.separator, cls.getName().split("\\."));

        final Path path = StringUtils.isBlank(classBasePath) ? Paths.get(clsPath) : Paths.get(classBasePath, clsPath);

        if (type == null) {
            final String prefix = path.toString();
            List<Path> list = Files.list(path.getParent())
                    .filter(x -> x.toString().startsWith(prefix))
                    .collect(Collectors.toList());

            if (list.size() > 1)
                throw new RuntimeException("有多个文件匹配,请手动指定type");

            type = Component.Type.textOf(FilenameUtils.getExtension(list.get(0).toString()));
        }
        if (type == null)
            throw new RuntimeException("无法找到有效的文件类型");


        submit(name, Paths.get(path.toString() + "." + type.text()), type, desc);
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
        Preconditions.checkArgument(Files.exists(path), "file not exists");

        String filename = path.getFileName().toString();

        name = name == null ? FilenameUtils.getBaseName(filename) : name;

        byte[] data = Files.readAllBytes(path);

        client.write(CommandCode.SUBMIT_COMPONENT, null, data, name, type, desc);

    }


    public void submit(String name, InputStream in,Component.Type type,String desc) throws IOException {
        byte[] data = IOUtils.toByteArray(in);

        client.write(CommandCode.SUBMIT_COMPONENT, null, data, name, type, desc);
    }


    public List<Component> select(Component.Type type) {
        ParameterizedType returnType = ParameterizedTypeImpl.make(List.class, new Type[]{Component.class}, null);
        return client.write(CommandCode.GET_COMPONENT_LIST, returnType,type);
    }

    public Component get(String name,Component.Type type) {

        return client.write(CommandCode.GET_COMPONENT, Component.class, name,type);
    }

    public void delete(String name,Component.Type type) {
        client.write(CommandCode.DELETE_COMPONENT, null, name,type);
    }


}
