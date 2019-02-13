package com.bh.spider.scheduler.component;

import com.bh.spider.transfer.Json;
import com.bh.spider.transfer.entity.Component;
import com.google.common.base.Preconditions;
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
    private final static int FIXED_ROW_SIZE = 500;


    private Path path;

    private Map<String, Position<Component>> components = new HashMap<>();


    private RandomAccessFile accessor;


    public Metadata(Path path) throws IOException {
        this.path = path;
        this.accessor = new RandomAccessFile(path.toFile(), "rw");
        if (Files.exists(path) && Files.size(path) > 0) {
            long pos = 0;

            while (pos < accessor.length()) {
                Position<Component> position = read0(pos);
                if (position != null)
                    components.put(position.data().getName(), position);

                pos += FIXED_ROW_SIZE;
            }
        }
    }


    public List<Component> components() {
        return components.values().stream().map(Position::data).collect(Collectors.toList());
    }

    private long write0(Component component,long pos) throws IOException {
        Preconditions.checkArgument(pos <= accessor.length(), "error metadata position");
        Preconditions.checkNotNull(component, "component can't null");
        Preconditions.checkArgument(StringUtils.isNotBlank(component.getName()), "component.name can't null");
        Preconditions.checkArgument(component.getName().length() <= 100, "component.name max length is 100");
        Preconditions.checkArgument(component.getDescription() == null || component.getDescription().length() < 200, "component.description max length is 200");

        if (pos >= 0 && pos != accessor.getFilePointer())
            accessor.seek(pos);

        pos = accessor.getFilePointer();
        accessor.writeBoolean(false);//先设置此行无效
        byte[] data = Json.get().writeValueAsBytes(component);
        accessor.writeInt(data.length);
        accessor.write(data);
        byte[] surplus = new byte[FIXED_ROW_SIZE - data.length - 4];//4字节表示有效字节长度的值,1为换行符
        accessor.write(surplus);
        long end = accessor.getFilePointer();
        accessor.seek(pos);
        accessor.writeBoolean(true);//写完 设置此行有效
        accessor.seek(end);

        return pos;
    }

    private Position<Component> read0(long pos) throws IOException {
        if (pos != accessor.getFilePointer())
            accessor.seek(pos);
        if (pos == accessor.length()) return null;

        if (!accessor.readBoolean())
            return null;

        int size = accessor.readInt();

        byte[] data = new byte[size];
        accessor.read(data);


        Component component = Json.get().readValue(data, Component.class);
        return new Position<>(component, pos);
    }


    public void write(Component component) throws IOException {

        long pos;
        Position<Component> position = components.get(component.getName());
        if (position != null) {
            long current = accessor.getFilePointer();
            pos = write0(component, position.pos());
            accessor.seek(current);
        } else {
            position = new Position<>();
            pos = write0(component, -1);
        }

        components.put(component.getName(), position.cover(component, pos));
    }

    public Component get(String name) {
        Position<Component> position = components.get(name);
        return position == null ? null : position.data();
    }

    public boolean delete(String name) throws IOException {
        Position<Component> position = components.get(name);
        if (position != null) {
            long end = accessor.getFilePointer();
            accessor.seek(position.pos());

            accessor.writeBoolean(false);
            accessor.seek(end);

            components.remove(name);

            return true;

        }
        return false;
    }

    /**
     * 对指定的name 进行wait
     * @param name
     */
    public boolean waitFor(String name) throws InterruptedException {
        final Position<Component> position = components.get(name);
        if(position==null) return false;

        synchronized (position){
            position.wait();
        }

        return true;
    }


    public boolean waitFor(String name,long timeout) throws InterruptedException {
        final Position<Component> position = components.get(name);
        if(position==null) return false;

        synchronized (position){
            position.wait(timeout);
        }

        return true;
    }


    private class Position<T> {
        private T data;
        private long pos;


        public Position(){}


        public Position(T data, long pos) {
            this.data = data;
            this.pos = pos;
        }

        public T data() {
            return data;
        }

        public long pos() {
            return pos;
        }


        public Position<T> cover(T data,long pos) {
            this.data = data;
            this.pos = pos;

            this.notifyAll();


            return this;
        }



    }
}
