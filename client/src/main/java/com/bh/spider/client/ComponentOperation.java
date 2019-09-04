package com.bh.spider.client;

import com.bh.common.utils.CommandCode;
import com.bh.spider.common.component.Component;
import com.google.common.base.Preconditions;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by lq on 7/9/17.
 */
public class ComponentOperation {
    private Communicator communicator = null;

    private String classBasePath;


    ComponentOperation(Communicator communicator, Properties properties) {
        this.communicator = communicator;
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

    public void submit(Path path) throws IOException {
        submit(null, path);
    }

    public void submit(Path path, Component.Type type) throws IOException {
        submit(path, type, null);
    }

    public void submit(Path path, Component.Type type, String desc) throws IOException {
        submit(null, path, type, desc);
    }

    public void submit(String name, Path path) throws IOException {
        submit(name, path, null);
    }

    public void submit(String name, Path path, String desc) throws IOException {
        submit(name, path, null, desc);
    }

    public void submit(String name, Class<?> cls, Component.Type type, String desc) throws IOException {

        Preconditions.checkArgument(cls != null, "you must special a valid class");

        String clsPath = String.join(File.separator, cls.getName().split("\\."));

        Path path = StringUtils.isBlank(classBasePath) ? Paths.get(clsPath) : Paths.get(classBasePath, clsPath);

        if (type == null) {
            final String prefix = path.toString();
            List<Path> list = Files.list(path.getParent())
                    .filter(x -> x.toString().startsWith(prefix))
                    .collect(Collectors.toList());

            if (list.size() > 1)
                throw new RuntimeException("有多个文件匹配,请手动指定type");

            path = list.get(0);


            type = Component.Type.textOf(FilenameUtils.getExtension(path.toString()));
        }
        if (type == null)
            throw new RuntimeException("无法找到有效的文件类型");


        submit(name, path, type, desc);
    }


    public void submit(String name, Path path, Component.Type type, String desc) throws IOException {

        assert path != null;

        Preconditions.checkArgument(type != null && type != Component.Type.UNKNOWN, "you must special a valid component type");
        Preconditions.checkArgument(Files.exists(path), "file not exists");

        String filename = path.getFileName().toString();

        name = name == null ? FilenameUtils.getBaseName(filename) : name;

        this.submit(name, Files.newInputStream(path), type, desc);
    }


    public void submit(String name, InputStream in,Component.Type type,String desc) throws IOException {
        byte[] data = IOUtils.toByteArray(in);

        communicator.write(CommandCode.SUBMIT_COMPONENT, null, data, name, type, desc);
    }


    public List<Component> select(Component.Type type) {
        ParameterizedType returnType = ParameterizedTypeImpl.make(List.class, new Type[]{Component.class}, null);
        return communicator.write(CommandCode.GET_COMPONENT_LIST, returnType,type);
    }

    public List<Component> select(){
        return select(null);
    }

    public Component get(String name) {

        return get(name,false);
    }


    public Component get(String name,boolean data){
        return communicator.write(CommandCode.GET_COMPONENT,Component.class,name,data);
    }

    public void delete(String name) {
        communicator.write(CommandCode.DELETE_COMPONENT, null, name);
    }


}
