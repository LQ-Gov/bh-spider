package com.bh.spider.scheduler.component;

import com.bh.spider.transfer.Json;
import com.bh.spider.transfer.entity.Component;
import org.apache.commons.lang3.StringUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Metadata {


    private Path path;

    private Map<String,Position<Component>> components = new HashMap<>();


    private RandomAccessFile accessor;



    public Metadata(Path path) throws IOException {
        this.path = path;
        this.accessor = new RandomAccessFile(path.toFile(), "rw");
        if (Files.exists(path) && Files.size(path) > 0) {
            try {
                do {
                    long pos = this.accessor.getFilePointer();
                    boolean valid = accessor.readBoolean();
                    String line = accessor.readLine();
                    if (!valid) continue;
                    if (StringUtils.isBlank(line)) break;

                    line = new String( line.getBytes(StandardCharsets.ISO_8859_1),Charset.defaultCharset());

                    Component component = Json.get().readValue(line, Component.class);
                    components.put(component.getName(), new Position<>(component, pos));
                } while (true);
            } catch (EOFException e) {
            }
        }
    }



    public List<Component> components() {
        return components.values().stream().map(Position::data).collect(Collectors.toList());
    }


    public void write(Component component) throws IOException {
        long pos = accessor.getFilePointer();
        accessor.writeBoolean(true);
        accessor.write(Json.get().writeValueAsBytes(component));
        accessor.write("\n".getBytes());

        components.put(component.getName(), new Position<>(component, pos));
    }

    public Component get(String name){
        Position<Component> position = components.get(name);
        return position==null?null:position.data();
    }

    public boolean delete(String name) throws IOException {
        Position<Component> position = components.get(name);
        if(position!=null){
            long end = accessor.getFilePointer();
            accessor.seek(position.pos());

            accessor.writeBoolean(false);
            accessor.seek(end);

            components.remove(name);

            return true;

        }
        return false;
    }


    private class Position<T>{
        private T data;
        private long pos;


        public Position(T data,long pos){
            this.data = data;
            this.pos = pos;
        }

        public T data(){ return data;}
        public long pos(){return pos;}
    }
}
